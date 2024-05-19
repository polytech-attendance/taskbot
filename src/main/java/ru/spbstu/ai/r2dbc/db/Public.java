/*
 * This file is generated by jOOQ.
 */
package ru.spbstu.ai.r2dbc.db;


import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import ru.spbstu.ai.r2dbc.db.tables.Owner;
import ru.spbstu.ai.r2dbc.db.tables.RecurringTask;
import ru.spbstu.ai.r2dbc.db.tables.Task;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.owner</code>.
     */
    public final Owner OWNER = Owner.OWNER;

    /**
     * The table <code>public.recurring_task</code>.
     */
    public final RecurringTask RECURRING_TASK = RecurringTask.RECURRING_TASK;

    /**
     * The table <code>public.task</code>.
     */
    public final Task TASK = Task.TASK;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Owner.OWNER,
            RecurringTask.RECURRING_TASK,
            Task.TASK
        );
    }
}
