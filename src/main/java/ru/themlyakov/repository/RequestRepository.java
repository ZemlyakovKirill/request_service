package ru.themlyakov.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.themlyakov.entity.Request;
import ru.themlyakov.entity.User;
import ru.themlyakov.util.RequestStatus;

import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request,Long>{

    Optional<Request> findRequestByIdIsAndStatusIs(Long id, RequestStatus status);
    Page<Request> findAllByStatusIs(RequestStatus status, Pageable pageable);

    Page<Request> findAllByUserIs(User user,Pageable pageable);

    Page<Request> findAllByUserIsAndStatusIs(User user,RequestStatus status,Pageable pageable);

    Page<Request> findAllByNameContainsIgnoreCaseAndStatusIs(String name,RequestStatus status,Pageable pageable);
}
