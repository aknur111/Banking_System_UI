package com.example.bank.domain.card.factory;

import com.example.bank.domain.account.model.Account;
import com.example.bank.domain.account.repository.AccountRepository;
import com.example.bank.domain.card.model.Card;
import com.example.bank.domain.card.model.CardStatus;
import com.example.bank.domain.card.model.CardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DebitCardFactory implements CardFactory {

    private final AccountRepository accountRepository;

    @Override
    public Card createCard(long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found " + accountId));

        String cardNumber = generateCardNumber();
        LocalDate expiry = LocalDate.now().plusYears(3);

        return Card.builder()
                .account(account)
                .cardNumber(cardNumber)
                .expiryMonth(expiry.getMonthValue())
                .expiryYear(expiry.getYear())
                .cvv(String.format("%03d", new Random().nextInt(1000)))
                .status(CardStatus.ACTIVE)
                .type(CardType.DEBIT)
                .build();
    }

    private static String generateCardNumber() {
        Random random = new Random();
        String prefix = "400000";
        int length = 16;

        int bodyLen = length - prefix.length() - 1;
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < bodyLen; i++) {
            sb.append(random.nextInt(10));
        }

        int check = computeLuhn(sb.toString());
        sb.append(check);

        String pan = sb.toString();
        if (pan.length() != length) {
            throw new IllegalStateException("generated PAN has wrong length");
        }
        if (!isValid(pan)) {
            throw new IllegalStateException("generated PAN failed Luhn check");
        }
        return pan;
    }

    private static boolean isValid(String pan) {
        int sum = 0;
        boolean doubleDigit = false;
        for (int i = pan.length() - 1; i >= 0; i--) {
            int d = pan.charAt(i) - '0';
            if (doubleDigit) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            doubleDigit = !doubleDigit;
        }
        return sum % 10 == 0;
    }

    private static int computeLuhn(String numberWithoutCheckDigit) {
        int sum = 0;
        boolean doubleDigit = true;
        for (int i = numberWithoutCheckDigit.length() - 1; i >= 0; i--) {
            int d = numberWithoutCheckDigit.charAt(i) - '0';
            if (doubleDigit) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            doubleDigit = !doubleDigit;
        }
        return (10 - (sum % 10)) % 10;
    }
}
