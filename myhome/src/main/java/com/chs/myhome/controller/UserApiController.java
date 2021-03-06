package com.chs.myhome.controller;

import com.chs.myhome.model.Board;
import com.chs.myhome.model.QUser;
import com.chs.myhome.model.User;
import com.chs.myhome.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
class UserApiController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    private UserRepository repository;

    @GetMapping("/users")
    Iterable<User> all(@RequestParam(required = false) String method, @RequestParam(required = false) String text) {
        LOGGER.info("all 출력!");

        Iterable<User> users = null;
        if ("query".equals(method)) {
            users = repository.findByUsernameQuery(text);
        } else if("nativeQuery".equals(method)){
            users = repository.findByUsernameNativeQuery(text);
        } else if("querydsl".equals(method)){
            QUser user = QUser.user;
//            BooleanExpression b = user.username.contains(text);
//            if(true){
//                b = b.and(user.username.eq("HI"));
//            }
            Predicate predicate = user.username.contains(text);
            users = repository.findAll(predicate);
        } else if ("querydslCustom".equals(method)){
            users = repository.findByUsernameCustom(text);
        } else if("jdbc".equals(method)){
            users = repository.findByUsernameJdbc(text);
        } else {
            users = repository.findAll();
        }
//       log.debug("getBoards().size() 호출전");
//       log.debug("getBoards().size() : {}", users.get(0).getBoards().size());
//       log.debug("getBoards().size() 호출후");
        return users;
    }

    @PostMapping("/users")
    User newUser(@RequestBody User newUser) {
        LOGGER.info("newUser 출력!");

        return repository.save(newUser);
    }

    // Single item

    @GetMapping("/users/{id}")
    User one(@PathVariable Long id) {
        LOGGER.info("one 출력!");

        return repository.findById(id).orElse(null);
    }

    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) {
        LOGGER.info("replaceUser 출력!");

        return repository.findById(id)
                .map(user -> {
//                    user.setTitle(newUser.getTitle());
//                    user.setContent(newUser.getContent());
//                    user.setBoards(newUser.getBoards());
                    user.getBoards().clear();
                    user.getBoards().addAll(newUser.getBoards());
                    for(Board board : user.getBoards()){
                        board.setUser(user);
                    }
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        LOGGER.info("deleteUser 출력!");

        repository.deleteById(id);
    }
}
