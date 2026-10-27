package br.com.tagarelas.application.adapters.persistence;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import br.com.tagarelas.services.ports.OtpRepository;

@Repository 
public class OtpRepositoryAdapter implements OtpRepository {

    private final Connection conn;

    public OtpRepositoryAdapter(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(UUID userId, String code, long expirationInSeconds) {

        long expiresAt = Instant.now().plusSeconds(expirationInSeconds).getEpochSecond();

        var savedOtp =  RethinkDB.r.hashMap("id",userId.toString())
        .with("code", code)
        .with("expiresAt", expiresAt);
        RethinkDB.r.table("otps").insert(savedOtp).optArg("conflict", "replace").run(conn);
    }

    @Override
    public void delete(UUID userId) {
        RethinkDB.r.table("otps")
        .get(userId.toString())
        .delete()
        .run(conn);
    }

    @Override
    public Optional<String> get(UUID userId) {
       Map<?,?> doc = RethinkDB.r.table("otps").get(userId.toString()).run(conn,Map.class).first();

       if(doc == null){
        return Optional.empty();
       }

     long expiresAt = ((Number) doc.get("expiresAt")).longValue();

       if(Instant.now().getEpochSecond() > expiresAt){
        delete(userId);
        return Optional.empty();
       }
       return Optional.of((String) doc.get("code"));
    }

}
