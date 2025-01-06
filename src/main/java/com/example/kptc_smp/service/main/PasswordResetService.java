package com.example.kptc_smp.service.main;

import com.example.kptc_smp.dto.ResponseDto;
import com.example.kptc_smp.dto.auth.PasswordResetDto;
import com.example.kptc_smp.dto.email.EmailDto;
import com.example.kptc_smp.entity.main.PasswordReset;
import com.example.kptc_smp.entity.main.User;
import com.example.kptc_smp.entity.main.UserInformation;
import com.example.kptc_smp.exception.auth.PasswordResetDateExpiredException;
import com.example.kptc_smp.exception.auth.PasswordResetUUIDNotFoundException;
import com.example.kptc_smp.exception.user.UserNotFoundException;
import com.example.kptc_smp.repository.main.PasswordResetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetRepository passwordResetRepository;
    private final UserService userService;
    private final UserInformationService userInformationService;
    private final EmailService emailService;
    private final PasswordService passwordService;

    @Value("${password.reset.base.url}")
    private String passwordResetBaseUrl;

    @Transactional
    public ResponseDto createPasswordResetLink(EmailDto emailDto) {
        UserInformation userInformation = userInformationService.findWithUserByEmail(emailDto.getEmail()).orElseThrow(UserNotFoundException::new);
        UUID linkUUID = UUID.randomUUID();
        createPasswordReset(userInformation.getUser(), linkUUID);
        String link = passwordResetBaseUrl + linkUUID;
        return emailService.sendPasswordResetLink(emailDto.getEmail(),link);
    }

    public void createPasswordReset(User user, UUID linkUUID) {
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setLinkUUID(linkUUID);
        passwordReset.setUser(user);
        passwordReset.setExpiryDate();
        passwordResetRepository.save(passwordReset);
    }

    @Transactional
    public ResponseDto resetPassword(UUID linkUUID, PasswordResetDto passwordResetDto) {
        PasswordReset passwordReset = passwordResetRepository.findByLinkUUID(linkUUID).orElseThrow(PasswordResetUUIDNotFoundException::new);
        if (isDateExpired(passwordReset)) {
            throw new PasswordResetDateExpiredException();
        }
        passwordService.validatePasswordEquals(passwordResetDto.getPassword(), passwordResetDto.getConfirmPassword());
        changeUserPassword(passwordReset,passwordResetDto);
        return new ResponseDto("Пароль изменен");
    }

    private boolean isDateExpired(PasswordReset passwordReset) {
        Calendar cal = Calendar.getInstance();
        return passwordReset.getExpiryDate().before(cal.getTime());
    }

    private void changeUserPassword(PasswordReset passwordReset, PasswordResetDto passwordResetDto){
        User user = passwordReset.getUser();
        user.setPassword(passwordService.encodePassword(passwordResetDto.getPassword()));
        userService.saveUser(user);
    }
}