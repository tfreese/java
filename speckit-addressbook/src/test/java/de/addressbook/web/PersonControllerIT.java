package de.addressbook.web;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * Contract-Tests fuer {@code POST /api/persons} und {@code GET /api/persons/{id}} gemaess
 * {@code contracts/openapi.yaml} (AK-001-005, User Story 1).
 * Constitution Principle VI: testbare Akzeptanzkriterien; research.md Entscheidung 8.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerIT {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    void createPersonReturns201WithLocationAndBody() throws Exception {
        final String body = objectMapper.writeValueAsString(new PersonRequest("Max", "Mustermann"));

        mockMvc.perform(post("/api/persons").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/api/persons/")))
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.firstName", is("Max")))
                .andExpect(jsonPath("$.lastName", is("Mustermann")))
                .andExpect(jsonPath("$.version", is(0)));
    }

    @Test
    void createPersonWithBlankFirstNameReturns400() throws Exception {
        final String body = objectMapper.writeValueAsString(new PersonRequest("   ", "Mustermann"));

        mockMvc.perform(post("/api/persons").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
    }

    @Test
    void deletePersonByIdReturns204OnSuccessAndSubsequentGetReturns404() throws Exception {
        final String location = createPersonAndGetLocation("Delete", "Erfolg");
        final long id = idFromLocation(location);

        mockMvc.perform(delete("/api/persons/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }

    @Test
    void deletePersonByIdReturns404ForUnknownId() throws Exception {
        mockMvc.perform(delete("/api/persons/{id}", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }

    @Test
    void deletePersonByIdReturns404WhenCalledTwice() throws Exception {
        final String location = createPersonAndGetLocation("Delete", "Zweifach");
        final long id = idFromLocation(location);

        mockMvc.perform(delete("/api/persons/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/persons/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }

    @Test
    void getPersonByIdReturns200ForExistingEntry() throws Exception {
        final String createBody = objectMapper.writeValueAsString(new PersonRequest("Bea", "Anders"));

        final String location = mockMvc.perform(
                        post("/api/persons").contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Bea")))
                .andExpect(jsonPath("$.lastName", is("Anders")));
    }

    @Test
    void getPersonByIdReturns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/persons/{id}", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }

    @Test
    void searchPersonsWithMatchingQueryReturnsFilteredResultsSortedByLastNameThenFirstName()
            throws Exception {
        final String uniqueLastName = "ZQTest" + UUID.randomUUID().toString().replace("-", "");

        createPerson("Bea", uniqueLastName);
        createPerson("Anna", uniqueLastName);

        mockMvc.perform(get("/api/persons").param("q", uniqueLastName.toLowerCase()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Anna")))
                .andExpect(jsonPath("$[1].firstName", is("Bea")));
    }

    @Test
    void searchPersonsWithUnmatchedQueryReturnsEmpty() throws Exception {
        final String neverUsedQuery = "NoMatch" + UUID.randomUUID();

        mockMvc.perform(get("/api/persons").param("q", neverUsedQuery))
                .andExpect(status().isOk())
                // .andExpect(jsonPath("$", empty()))
                .andExpect(jsonPath("$.content", empty()))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)))
        ;
    }

    @Test
    void searchPersonsWithoutQueryReturns200WithAllEntries() throws Exception {
        final String createBody = objectMapper.writeValueAsString(new PersonRequest("Clara", "Ohne-Query-Test"));
        mockMvc.perform(post("/api/persons").contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.not(empty())));
    }

    @Test
    void updatePersonByIdReturns200OnSuccess() throws Exception {
        final String location = createPersonAndGetLocation("Update", "Erfolg");
        final long id = idFromLocation(location);

        final String updateBody = objectMapper.writeValueAsString(
                new PersonUpdateRequest("Update", "Erfolgreich", 0L));

        mockMvc.perform(put("/api/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON).content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Update")))
                .andExpect(jsonPath("$.lastName", is("Erfolgreich")))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    void updatePersonByIdReturns404ForUnknownId() throws Exception {
        final String updateBody = objectMapper.writeValueAsString(
                new PersonUpdateRequest("Egal", "Egal", 0L));

        mockMvc.perform(put("/api/persons/{id}", 999_999L)
                        .contentType(MediaType.APPLICATION_JSON).content(updateBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("NOT_FOUND")));
    }

    @Test
    void updatePersonByIdReturns409ForStaleVersion() throws Exception {
        final String location = createPersonAndGetLocation("Update", "Konflikt");
        final long id = idFromLocation(location);

        final String firstUpdateBody = objectMapper.writeValueAsString(
                new PersonUpdateRequest("Update", "Konflikt-Erste-Aenderung", 0L));
        mockMvc.perform(put("/api/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON).content(firstUpdateBody))
                .andExpect(status().isOk());

        final String staleUpdateBody = objectMapper.writeValueAsString(
                new PersonUpdateRequest("Update", "Konflikt-Zweite-Aenderung", 0L));

        mockMvc.perform(put("/api/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON).content(staleUpdateBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("CONFLICT")));
    }

    @Test
    void updatePersonByIdWithBlankFirstNameReturns400() throws Exception {
        final String location = createPersonAndGetLocation("Update", "Validierung");
        final long id = idFromLocation(location);

        final String updateBody = objectMapper.writeValueAsString(
                new PersonUpdateRequest("   ", "Validierung", 0L));

        mockMvc.perform(put("/api/persons/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON).content(updateBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
    }

    private void createPerson(final String firstName, final String lastName) throws Exception {
        final String body = objectMapper.writeValueAsString(new PersonRequest(firstName, lastName));

        mockMvc.perform(post("/api/persons").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }

    private String createPersonAndGetLocation(final String firstName, final String lastName) throws Exception {
        final String body = objectMapper.writeValueAsString(new PersonRequest(firstName, lastName));

        return mockMvc.perform(post("/api/persons").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    private long idFromLocation(final String location) {
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }
}
