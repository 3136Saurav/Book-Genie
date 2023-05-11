package com.learn.bookgenie.book;

import com.learn.bookgenie.userbooks.UserBooks;
import com.learn.bookgenie.userbooks.UserBooksPrimaryKey;
import com.learn.bookgenie.userbooks.UserBooksRepository;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class BookController {

    private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserBooksRepository userBooksRepository;

    @GetMapping("/book/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            String coverImageUrl = "/images/book-not-found.png";
            if (book.getCoverIds() != null && book.getCoverIds().size() > 0) {
                coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-L.jpg";
            }
            model.addAttribute("coverImage", coverImageUrl);
            model.addAttribute("book", book);

            if (principal != null && StringUtils.isNotBlank(principal.getAttribute("name"))) {
                String loginId = principal.getAttribute("name");
                System.out.println("Principal attributes: " + principal.getAttributes());
                model.addAttribute("loginId", loginId);

                UserBooksPrimaryKey key = new UserBooksPrimaryKey();
                key.setBookId(bookId);
                key.setUserId(loginId);
                Optional<UserBooks> optionalUserBooks = userBooksRepository.findById(key);
                if (optionalUserBooks.isPresent()) {
                    model.addAttribute("userBooks", optionalUserBooks.get());
                } else {
                    model.addAttribute("userBooks", new UserBooks());
                }
            }

            return "book";
        }
        return "book-not-found";
    }
}
