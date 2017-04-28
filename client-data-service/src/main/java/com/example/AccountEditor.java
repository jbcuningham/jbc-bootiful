package com.example;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;


@SpringComponent
@UIScope
public class AccountEditor extends VerticalLayout {

    private final AccountRepository repository;

    /**
     * The currently edited account
     */
    private Account account;

    /* Fields to edit properties in Account entity */
    TextField accountBalance = new TextField("Account Balance");
    TextField clientId = new TextField("Client Id");




    /* Action buttons */
    Button save = new Button("Save", FontAwesome.SAVE);
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", FontAwesome.TRASH_O);
    CssLayout actions = new CssLayout(save, cancel, delete);

    Binder<Account> binder = new Binder<>(Account.class);


    @Autowired
    public AccountEditor(AccountRepository repository) {
        this.repository = repository;

        addComponents(accountBalance, clientId, actions);



        // bind using naming convention
        binder.bindInstanceFields(this);


        // Configure and style components
        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> repository.save(account));
        delete.addClickListener(e -> repository.delete(account));
        cancel.addClickListener(e -> editAccount(account));
        setVisible(false);
    }

    public interface ChangeHandler {

        void onChange();
    }

    public final void editAccount(Account c) {
        if (c == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = c.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            account = repository.findOne(c.getId());
        }
        else {
            account = c;
        }
        cancel.setVisible(persisted);

        // Bind account properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(account);

        setVisible(true);

        // A hack to ensure the whole form is visible
        save.focus();
        // Select all text in firstName field automatically
//        accountBalance.selectAll();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        save.addClickListener(e -> h.onChange());
        delete.addClickListener(e -> h.onChange());
    }

}
