package transfer.web.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountTransferRequest {
    private String accountFrom;
    private String accountTo;
    private BigDecimal amount;
}
