package com.example.ReservationTimings.Services;


import com.example.ReservationTimings.Document.DatabaseSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
public class SequenceGeneratorService {


    private static final Logger log = LogManager.getLogger(SequenceGeneratorService.class.getName());


    private MongoOperations mongoOperations;

    @Autowired
    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public long generateSequence(String seqName) {

        log.info("SEQUENCE GENERATOR SERVICE:Entered into the generate seq service ");
        Query query = new Query(Criteria.where("id").is(seqName));
        Update update = new Update().inc("seq", 1);
        DatabaseSequence counter;
        counter = mongoOperations.findAndModify(query, update, options().returnNew(true).upsert(true), DatabaseSequence.class);
        log.debug("SEQUENCE GENERATOR SERVICE:Exited from the generate seq service ");
        return !Objects.isNull(counter) ? counter.getSeq() : 1;

    }
}