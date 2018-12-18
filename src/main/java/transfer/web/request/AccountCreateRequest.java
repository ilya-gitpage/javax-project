package transfer.web.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountCreateRequest {
    private String accountNumber;
    private BigDecimal amount;
}