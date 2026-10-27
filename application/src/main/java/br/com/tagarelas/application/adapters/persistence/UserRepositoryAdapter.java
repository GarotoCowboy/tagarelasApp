package br.com.tagarelas.application.adapters.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import br.com.tagarelas.core.User;
import br.com.tagarelas.services.ports.UserRepository;

@Repository 
public class UserRepositoryAdapter implements UserRepository{

    private final Connection conn;

    

    public UserRepositoryAdapter(Connection conn) {
        this.conn = conn;
    }

    @Override
    public User save(User user) {
        
        RethinkDB.r.table("users").insert(user).optArg("conflict", "replace")
        .run(conn);

        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user = RethinkDB.r.table("users").get(id.toString()).run(conn,User.class).first();

        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = RethinkDB.r.table("users").filter(RethinkDB.r.hashMap("email",email)).run(conn,User.class).first();

        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        User user = RethinkDB.r.table("users").filter(RethinkDB.r.hashMap("username",username)).run(conn,User.class).first();

        return Optional.ofNullable(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        
        return findByEmail(email).isPresent();
    }

    @Override
    public boolean existsByUsername(String username) {

        return findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {

        return RethinkDB.r.table("users").filter(RethinkDB.r.hashMap("phoneNumber",phoneNumber)).run(conn,User.class).first() != null;


    }

}
