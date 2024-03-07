package ru.themlyakov.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.MissingServletRequestParameterException;
import ru.themlyakov.util.RequestStatus;

import java.util.Date;

@Entity(name = "requests")
@Data
@NoArgsConstructor
@JsonIgnoreProperties("user")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @Column(name = "create_date")
    private Date creationDate;
    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(name = "phone", nullable = false)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(referencedColumnName = "id",
            name = "user_id", nullable = false)
    private User user;

    public Request(String name, String message, String phoneNumber, User user) {
        this.name = name;
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }

    public void edit(Request request) throws MissingServletRequestParameterException {
        if (request.name != null) this.name = request.name;
        else throw new MissingServletRequestParameterException("name", "String");
        if (request.message != null) this.message = request.message;
        else throw new MissingServletRequestParameterException("message", "String");
        if (request.phoneNumber != null) this.phoneNumber = request.phoneNumber;
        else throw new MissingServletRequestParameterException("phoneNumber", "String");
    }
}
