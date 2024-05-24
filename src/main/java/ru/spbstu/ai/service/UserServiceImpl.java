 package ru.spbstu.ai.service;

 import org.jooq.DSLContext;
 import org.springframework.stereotype.Service;
 import reactor.core.publisher.Mono;
 import ru.spbstu.ai.entity.User;
 import ru.spbstu.ai.r2dbc.db.tables.Owner;

 @Service
public class UserServiceImpl implements UserService {
    private final DSLContext ctx;

     public UserServiceImpl(DSLContext ctx) {
         this.ctx = ctx;
     }

     @Override
    public Mono<User> create(long telegramId) {
        return Mono.from(ctx.insertInto(Owner.OWNER, Owner.OWNER.TELEGRAM_ID).values((int) telegramId))
                .then(getUser(telegramId));
    }

    @Override
    public Mono<User> getUser(long telegramId) {
        return Mono.from(ctx.select(Owner.OWNER.OWNER_ID).from(Owner.OWNER).where(Owner.OWNER.TELEGRAM_ID.eq((int)telegramId)))
                .map(x -> new User(x.get(Owner.OWNER.OWNER_ID)));
    }
}
