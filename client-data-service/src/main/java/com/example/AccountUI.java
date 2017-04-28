package com.example;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@SpringUI( path = "ui/accounts")
public class AccountUI extends UI {

    private final AccountRepository repo;

    private final AccountEditor editor;

    final Grid<Account> grid;

    final TextField filter;

    private final Button addNewBtn;

    @Autowired
    public AccountUI(AccountRepository repo, AccountEditor editor) {
        this.repo = repo;
        this.editor = editor;
        this.grid = new Grid<>(Account.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New account");

    }

    @Override
    protected void init(VaadinRequest request) {

        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
        setContent(mainLayout);

        grid.setHeight(75, Unit.PERCENTAGE);
        grid.setColumns("id", "accountBalance", "clientId");

        filter.setPlaceholder("Filter by client id");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listAccounts(e.getValue()));

        // Connect selected Account to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editAccount(e.getValue());
        });

        // Instantiate and edit new Account the new button is clicked
        addNewBtn.addClickListener(e -> editor.editAccount(new Account("","")));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listAccounts(filter.getValue());
        });

        // Initialize listing
        listAccounts(null);
    }

    private void listAccounts() {
        grid.setItems(repo.findAll());
        //grid.setDataProvider(FetchItemsCallback<T>, SerializableSupplier<Integer>)
    }

    // tag::listAccounts[]
    void listAccounts(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findAll());
        } else {
            grid.setItems(repo.findByClientId(filterText));
        }
    }
    // end::listAccounts[]

}
