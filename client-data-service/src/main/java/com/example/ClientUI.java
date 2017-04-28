package com.example;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;


@SpringUI( path = "ui/clients")
public class ClientUI extends UI {

//        setContent(new Button("Click me", e ->
//                Notification.show("Hello Spring+Vaadin user.")));

    private final ClientRepository repo;

    private final ClientEditor editor;

    final Grid<Client> grid;

    final TextField filter;

    private final Button addNewBtn;

    @Autowired
    public ClientUI(ClientRepository repo, ClientEditor editor) {
        this.repo = repo;
        this.editor = editor;
        this.grid = new Grid<>(Client.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New client");

    }

    @Override
    protected void init(VaadinRequest request) {

        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
        VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
        setContent(mainLayout);

        grid.setHeight(75, Unit.PERCENTAGE);
        grid.setColumns("id", "firstName", "lastName");

        filter.setPlaceholder("Filter by last name");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> listClients(e.getValue()));

        // Connect selected Client to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editClient(e.getValue());
        });

        // Instantiate and edit new Client the new button is clicked
        addNewBtn.addClickListener(e -> editor.editClient(new Client("", "")));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listClients(filter.getValue());
        });

        // Initialize listing
        listClients(null);

        //        setContent(grid);
//        listClients();
//        TextField filter = new TextField();
//        filter.setPlaceholder("Filter by last name");
//        filter.setValueChangeMode(ValueChangeMode.LAZY);
//        filter.addValueChangeListener(e -> listClients(e.getValue()));
//        VerticalLayout mainLayout = new VerticalLayout(filter, grid);
//        setContent(mainLayout);
    }

    private void listClients() {
        grid.setItems(repo.findAll());
        //grid.setDataProvider(FetchItemsCallback<T>, SerializableSupplier<Integer>)
    }

    // tag::listClients[]
    void listClients(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            grid.setItems(repo.findAll());
        } else {
            grid.setItems(repo.findByLastNameStartsWithIgnoreCase(filterText));
        }
    }
    // end::listClients[]

}
