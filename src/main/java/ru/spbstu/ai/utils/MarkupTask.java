package ru.spbstu.ai.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.spbstu.ai.entity.Task;

import java.util.ArrayList;
import java.util.List;

public class MarkupTask {

    public static InlineKeyboardMarkup markupForTask(Task task){
        InlineKeyboardButton doneButton = new InlineKeyboardButton("DONE ✅");
        doneButton.setCallbackData("done task " + task.id());

        // Emoji clocks
        InlineKeyboardButton inProgressButton = new InlineKeyboardButton("IN PROGRESS \uD83D\uDD53");
        inProgressButton.setCallbackData("in_progress task " + task.id());

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(doneButton);
        row1.add(inProgressButton);

        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(new InlineKeyboardRow(row1));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        return markup;
    }
}
