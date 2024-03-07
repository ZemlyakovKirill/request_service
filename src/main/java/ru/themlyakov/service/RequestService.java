package ru.themlyakov.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;
import ru.themlyakov.cloud.PhoneValidator;
import ru.themlyakov.entity.Request;
import ru.themlyakov.entity.User;
import ru.themlyakov.repository.RequestRepository;
import ru.themlyakov.util.RequestStatus;
import ru.themlyakov.util.UserRequestView;
import ru.themlyakov.util.exception.IncorrectDataException;
import ru.themlyakov.util.exception.NotFoundException;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.themlyakov.util.RequestStatus.*;

@Service
public class RequestService {
    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PhoneValidator phoneValidator;

    public boolean createRequest(Request request) throws JsonProcessingException {
        request.setStatus(RequestStatus.DRAFT);
        String phoneNumber = request.getPhoneNumber();
        String response = phoneValidator.validatePhone(List.of(phoneNumber));
        logger.info(response);
        boolean phoneValid = isPhoneValid(response);
        if (phoneValid) {
            request.setStatus(RequestStatus.DRAFT);
            request.setCreationDate(new Date());
            request.setPhoneNumber(decoratedPhoneNumber(response));
            requestRepository.save(request);
            return true;
        }
        return false;
    }

    private int decodePhoneStatus(String response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response).get(0);
        return node.get("qc").asInt(2);
    }

    private String decoratedPhoneNumber(String response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response).get(0);
        return node.get("phone").asText("");
    }

    private boolean isPhoneValid(String response) throws JsonProcessingException {
        int status = decodePhoneStatus(response);
        logger.info(String.valueOf(status));
        return switch (status) {
            case 0, 7, 3 -> true;
            default -> throw new IncorrectDataException("Phone is not valid");
        };
    }

    public Page<Request> findAll(Pageable pageable) {
        return requestRepository.findAll(pageable);
    }

    public Page<Request> findAllFilteredByStatus(RequestStatus status, Pageable pageable) {
        return requestRepository.findAllByStatusIs(status, pageable);
    }

    @Transactional
    public Request sendRequest(Authentication authentication, Long id) {
        User user = userService.findByUsername(authentication.getName());
        List<Request> requests = user.getRequests();
        Request foundRequest = requests.stream()
                .filter(request -> request.getId().equals(id))
                .findAny().orElseThrow(() -> new NotFoundException("Request not found"));
        foundRequest.setStatus(SEND);
        return requestRepository.save(foundRequest);
    }


    public List<Request> findUserRequests(User user, Integer page, Sort.Direction direction, RequestStatus status) {
        if (status == null) {
            return requestRepository
                    .findAllByUserIs(user,
                            PageRequest.of(page, 5,
                                    Sort.by(direction, "creationDate")))
                    .stream().collect(Collectors.toList());
        } else {
            return requestRepository
                    .findAllByUserIsAndStatusIs(user, status,
                            PageRequest.of(page, 5,
                                    Sort.by(direction, "creationDate")))
                    .stream().collect(Collectors.toList());
        }
    }

    @Transactional
    public Request editDraftRequest(Authentication authentication, Request request) throws JsonProcessingException, MissingServletRequestParameterException {
        User user = userService.findByUsername(authentication.getName());
        Request foundRequest = user.getRequests()
                .stream()
                .filter(req -> req.getId().equals(request.getId()))
                .filter(req -> req.getStatus().equals(RequestStatus.DRAFT))
                .findAny().orElseThrow(() -> new NotFoundException("Request not found"));
        if (request.getPhoneNumber()==null){
            throw new MissingServletRequestParameterException("phoneNumber","String");
        }
        String response = phoneValidator.validatePhone(List.of(request.getPhoneNumber()));
        if(isPhoneValid(response)){
            String phoneNumber = decoratedPhoneNumber(response);
            request.setPhoneNumber(phoneNumber);
            foundRequest.edit(request);
            userService.save(user);
            return foundRequest;
        }
        throw new IncorrectDataException("Phone is not valid");
    }

    private List<Request> getAllRequests(int page,RequestStatus status, Sort.Direction direction,String name) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(direction, "creationDate"));
        if(name == null){
            return requestRepository.findAllByStatusIs(status,pageable).stream().toList();
        }else{
            return requestRepository.findAllByNameContainsIgnoreCaseAndStatusIs(name,status,pageable).stream().toList();
        }
    }

    public List<Request> getSendRequests(int page, Sort.Direction direction,String name){
        return getAllRequests(page,SEND,direction,name);
    }

    public List<Request> getSendAcceptedDeclinedRequests(int page,RequestStatus status, Sort.Direction direction,String name){
        return switch (status) {
            case SEND, DECLINED, ACCEPTED -> getAllRequests(page, status, direction, name);
            default -> throw new IncorrectDataException("Request status cannot be DRAFT");
        };
    }



    @Transactional
    public UserRequestView findUsernameRequests(String username, int page, Sort.Direction direction) {
        User user = userService.findByUsernameContains(username);
        List<Request> requests = user.getRequests().stream()
                .filter(request -> request.getStatus().equals(SEND))
                .sorted(direction == Sort.Direction.ASC
                        ? Comparator.comparing(Request::getCreationDate)
                        : Comparator.comparing(Request::getCreationDate).reversed())
                .toList();
        return new UserRequestView(user,requests);
    }

    public Request findRequestById(Long id) {
        return requestRepository.findById(id).orElseThrow(()-> new NotFoundException("Request not found"));
    }

    @Transactional
    public Request acceptOrDeclineRequest(Long id,RequestStatus status){
        Request request = requestRepository.findRequestByIdIsAndStatusIs(id,SEND).orElseThrow(() -> new NotFoundException("Request is not send or not found"));
        request.setStatus(status);
        return requestRepository.save(request);
    }

}
