package com.example.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "DB_PASSWORD=unused",
        "JWT_SECRET=test-secret-test-secret-test-secret-test-secret"
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class LedgerApiIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void fullFlow_register_createClient_createInvoice_dashboard() throws Exception {
        String token = register("Acme Studio", email());

        String clientId = JsonPath.read(
                mvc.perform(post("/api/clients").header("Authorization", bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"Globex\",\"email\":\"billing@globex.test\"}"))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString(),
                "$.id");

        mvc.perform(post("/api/invoices").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoiceJson(clientId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("INV-0001"))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.totalCents").value(31_099));

        mvc.perform(post("/api/invoices").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoiceJson(clientId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("INV-0002"));

        mvc.perform(get("/api/invoices").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].items.length()").value(2));

        mvc.perform(get("/api/dashboard").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.draftCents").value(62_198))
                .andExpect(jsonPath("$.invoiceCount").value(2))
                .andExpect(jsonPath("$.clientCount").value(1));
    }

    @Test
    void loginReturnsTokenAndRejectsWrongPassword() throws Exception {
        String email = email();
        register("Login Co", email);

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"correct-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateEmailIsRejected() throws Exception {
        String email = email();
        register("First", email);

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workspaceName\":\"Second\",\"email\":\"" + email
                                + "\",\"password\":\"correct-password\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void protectedEndpointsRequireAuthentication() throws Exception {
        mvc.perform(get("/api/clients")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/dashboard")).andExpect(status().isUnauthorized());
    }

    @Test
    void workspacesAreIsolated() throws Exception {
        String tokenA = register("Workspace A", email());
        String tokenB = register("Workspace B", email());

        String clientOfA = JsonPath.read(
                mvc.perform(post("/api/clients").header("Authorization", bearer(tokenA))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"Private client\"}"))
                        .andReturn().getResponse().getContentAsString(),
                "$.id");

        mvc.perform(get("/api/clients").header("Authorization", bearer(tokenB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mvc.perform(post("/api/invoices").header("Authorization", bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoiceJson(clientOfA)))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidInputIsRejectedWithFieldErrors() throws Exception {
        String token = register("Validation Co", email());

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workspaceName\":\"\",\"email\":\"not-an-email\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());

        mvc.perform(post("/api/invoices").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\":\"" + UUID.randomUUID()
                                + "\",\"issueDate\":\"2026-10-01\",\"dueDate\":\"2026-10-31\",\"items\":[]}"))
                .andExpect(status().isBadRequest());
    }

    private String register(String workspace, String email) throws Exception {
        String body = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workspaceName\":\"" + workspace + "\",\"email\":\"" + email
                                + "\",\"password\":\"correct-password\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private static String invoiceJson(String clientId) {
        return "{\"clientId\":\"" + clientId + "\",\"issueDate\":\"2026-10-01\",\"dueDate\":\"2026-10-31\","
                + "\"items\":[{\"description\":\"Design\",\"quantity\":2,\"unitPriceCents\":15050},"
                + "{\"description\":\"Hosting\",\"quantity\":1,\"unitPriceCents\":999}]}";
    }

    private static String email() {
        return "user-" + UUID.randomUUID() + "@example.test";
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
