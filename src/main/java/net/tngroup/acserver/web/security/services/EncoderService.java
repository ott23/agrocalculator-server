package net.tngroup.acserver.web.security.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EncoderService {

    BCryptPasswordEncoder encoder;

    public EncoderService() {
        this.encoder = new BCryptPasswordEncoder(10);
    }

    public String encode(String password) {
        return encoder.encode(password);
    }

    public BCryptPasswordEncoder getEncoder() {
        return encoder;
    }

}
