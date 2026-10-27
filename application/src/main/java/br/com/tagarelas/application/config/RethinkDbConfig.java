package br.com.tagarelas.application.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

@Configuration 
public class RethinkDbConfig {
   
    private final String host;

    private final int port;

    
    private final String database;

    
    public RethinkDbConfig(
         @Value("${rethinkdb.host}")
        String host,
        
         @Value("${rethinkdb.port}")
        int port, 
        
        @Value("${rethinkdb.database}")
        String database)
         {
        this.host = host;
        this.port = port;
        this.database = database;
    }


    @Bean(destroyMethod = "close")
    public Connection connect(){
      var conn = RethinkDB.r.connection().hostname(host).port(port).connect();

        RethinkDB.getResultMapper().registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        
      List<String> dbs = RethinkDB.r.dbList().run(conn, List.class).first();
        if(!dbs.contains(database)){
            RethinkDB.r.dbCreate(database).run(conn);
        }
        conn.use(database);
        
       List<String> requiredTables = List.of("users","otps","messages","friendships","guilds",
        "guild_invites","roles","groups","dm_channels","guild_channels");

        List<String> existingTables = RethinkDB.r.tableList().run(conn,List.class).first();

        for(String table :requiredTables){
            if(!existingTables.contains(table)){
                RethinkDB.r.tableCreate(table).run(conn);
            }
        }

        return conn;
    }
}
