package com.learn.bookgenie.home;

import com.learn.bookgenie.user.BooksByUser;
import com.learn.bookgenie.user.BooksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";

    @Autowired
    BooksByUserRepository booksByUserRepository;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null || StringUtils.isEmpty(principal.getAttribute("name"))) {
            return "index";
        }

        String userId = principal.getAttribute("name");
        Slice<BooksByUser> slice = booksByUserRepository.findAllById(userId, CassandraPageRequest.of(0, 100));
        List<BooksByUser> booksByUser = slice.getContent();

        booksByUser = booksByUser.stream().distinct().map(book -> {
            String coverImageUrl = "/images/no-image.jpg";
            if (book.getCoverIds() != null && book.getCoverIds().size() > 0) {
                coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-M.jpg";
            }
            book.setCoverUrl(coverImageUrl);
            return book;
        }).collect(Collectors.toList());

        model.addAttribute("books", booksByUser);
        return "home";
    }
}
