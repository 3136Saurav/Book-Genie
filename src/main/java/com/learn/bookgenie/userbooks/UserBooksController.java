package com.learn.bookgenie.userbooks;

import com.learn.bookgenie.book.Book;
import com.learn.bookgenie.book.BookRepository;
import com.learn.bookgenie.user.BooksByUser;
import com.learn.bookgenie.user.BooksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserBooksController {
    @Autowired
    private final UserBooksRepository userBooksRepository;

    @Autowired
    BooksByUserRepository booksByUserRepository;

    @Autowired
    BookRepository bookRepository;
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

        String bookId = formData.getFirst("bookId");
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()) {
            return new ModelAndView("redirect:/");
        }
        Book book = optionalBook.get();


        UserBooks userBooks = new UserBooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();
        key.setUserId(loginId);

        key.setBookId(bookId);
        userBooks.setKey(key);

        userBooks.setStartDate(LocalDate.parse(formData.getFirst("startDate")));
        userBooks.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        int rating = Integer.parseInt(formData.getFirst("rating"));
        userBooks.setRating(rating);
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        userBooksRepository.save(userBooks);

        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(loginId);
        booksByUser.setBookId(bookId);
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverIds());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
        booksByUser.setRating(rating);
        booksByUserRepository.save(booksByUser);

        return new ModelAndView("redirect:/book/" + bookId);
    }
}
