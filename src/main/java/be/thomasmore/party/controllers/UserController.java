package be.thomasmore.party.controllers;

import be.thomasmore.party.model.PartyAnimal;
import be.thomasmore.party.model.User;
import be.thomasmore.party.repositories.PartyAnimalRepository;
import be.thomasmore.party.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(AdminController.class);
    private String applicationName = "It's Party Time!!";

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartyAnimalRepository partyAnimalRepository;

    @GetMapping("/register")
    public String register(Principal principal, Model model) {
        if (principal != null) return "redirect:/parties";

        model.addAttribute("appName", applicationName);
        return "user/register";
    }

    @PostMapping("/register")
    public String registerPost(@RequestParam String userName,
                               @RequestParam String password,
                               @RequestParam String nickname,
                               Principal principal, Model model,
                               HttpServletRequest request) {
        logger.info(String.format("register %s\n", userName));

        if (principal == null && !userName.isBlank()) {
            Optional<User> userWithUserName = userRepository.findByUsername(userName);
            if (!userWithUserName.isPresent()) {
                User newUser = new User();
                newUser.setUsername(userName);
                String encode = passwordEncoder.encode(password);
                logger.info(String.format("password %s\n", encode));
                newUser.setPassword(encode);
                newUser.setRole("USER");
                userRepository.save(newUser);
                PartyAnimal newPartyAnimal = new PartyAnimal();
                newPartyAnimal.setName(nickname);
                newPartyAnimal.setUser(newUser);
                partyAnimalRepository.save(newPartyAnimal);

                autologin(userName, password);
            }
        }

        return "redirect:/parties";
    }

    private void autologin(String userName, String password) {
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(userName, password);

        try {
            Authentication auth = authenticationManager.authenticate(token);
            logger.info("authentication done - result is " + auth.isAuthenticated());
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }

    // Login form
    @RequestMapping("/login")
    public String login(Principal principal, Model model) {
        if (principal != null) return "redirect:/parties";
        model.addAttribute("appName", applicationName);
        return "user/login";
    }

    // Login form
    @RequestMapping("/logout")
    public String logout(Model model) {
        model.addAttribute("appName", applicationName);
        return "user/logout";
    }

}
