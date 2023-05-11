package com.learn.bookgenie.userbooks;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
public class UserBooksController {
    private final UserBooksRepository userBooksRepository;

    public UserBooksController(UserBooksRepository userBooksRepository) {
        this.userBooksRepository = userBooksRepository;
    }

    @PostMapping("/addUserBook")
    public ModelAndView addUserBook(@RequestBody MultiValueMap<String, String> formData, @AuthenticationPrincipal OAuth2User principal) {
        System.out.println("formData: " + formData);

        if (principal == null || StringUtils.isEmpty(principal.getAttribute("name"))) {
            return null;
        }
        String loginId = principal.getAttribute("name");
        UserBooks userBooks = new UserBooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();
        key.setUserId(loginId);
        String bookId = formData.getFirst("bookId");
        key.setBookId(bookId);
        userBooks.setKey(key);

        userBooks.setStartDate(LocalDate.parse(formData.getFirst("startDate")));
        userBooks.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBooks.setRating(Integer.parseInt(formData.getFirst("rating")));
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        userBooksRepository.save(userBooks);
        return new ModelAndView("redirect:/book/" + bookId);
    }
}
