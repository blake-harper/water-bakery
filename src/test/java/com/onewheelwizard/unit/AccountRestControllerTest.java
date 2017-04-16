package com.onewheelwizard.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onewheelwizard.bakery.controller.AccountRestController;
import com.onewheelwizard.bakery.data.AccountRepository;
import com.onewheelwizard.bakery.data.PurityReportRepository;
import com.onewheelwizard.bakery.data.WaterReportRepository;
import com.onewheelwizard.bakery.model.Account;
import com.onewheelwizard.bakery.model.constants.UserType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private WaterReportRepository waterReportRepository;
    @Mock
    private PurityReportRepository purityReportRepository;

    @InjectMocks
    private AccountRestController accountRestController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(accountRestController)
                .build();
    }

    // create

    @Test
    public void postAccount_ValidAccount_ReturnsOk() {

    }

    @Test
    public void postAccount_UsernameTaken_ReturnsBadRequest() throws Exception {
        Account validAccount = new Account("someuser", "somepassword", UserType.MANAGER,
                "some@email.co", "Mr.", "Atlanta");

        Account existingAccount = new Account("someuser", "otherpass", UserType.WORKER,
                "some1else@email.co", "Mrs.", "Atlanta");

        when(accountRepository.findByUsername(validAccount.getUsername())).thenReturn(
                Optional.of(existingAccount));

        mockMvc.perform(MockMvcRequestBuilders.
                post("/accounts/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(validAccount)))
                .andExpect(status().isBadRequest());

        verify(accountRepository, times(1)).findByUsername(validAccount.getUsername());
        verifyNoMoreInteractions(accountRepository);
    }

    // read section

    @Test
    public void getAccountByUsername_UsernameExists_ReturnsAccount() throws Exception {
        Account expected = new Account("someuser", "somepassword", UserType.MANAGER,
                "some@email.co", "Mr.", "Atlanta");

        when(accountRepository.findByUsername(expected.getUsername())).thenReturn(
                Optional.of(expected));
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/" + expected.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.username", is(expected.getUsername())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.userType", is(expected.getUserType().toString())))
                .andExpect(jsonPath("$.email", is(expected.getEmail())))
                .andExpect(jsonPath("$.title", is(expected.getTitle())))
                .andExpect(jsonPath("$.city", is(expected.getCity())));

        verify(accountRepository, times(1)).findByUsername(expected.getUsername());
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    public void getAccountByUsername_UsernameDoesNotExist_ReturnsNotFound() throws Exception {
        String username = "someUserThatDoesNotExist";

        when(accountRepository.findByUsername(username)).thenReturn(
                Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/" + username))
                .andExpect(status().isNotFound());

        verify(accountRepository, times(1)).findByUsername("someUserThatDoesNotExist");
        verifyNoMoreInteractions(accountRepository);
    }

    // delete section

    @Test
    public void deleteAccountByUsername_UsernameExists_DeletesAccountAndReturnsNoContent() throws Exception {
        Account expected = new Account("someuser", "somepassword", UserType.MANAGER,
                "some@email.co", "Mr.", "Atlanta");

        when(accountRepository.findByUsername(expected.getUsername())).thenReturn(
                Optional.of(expected));
        when(accountRepository.deleteByUsername(expected.getUsername())).thenReturn(1L);
        when(waterReportRepository.deleteAllByAccountUsername(expected.getUsername())).thenReturn(1L);
        when(purityReportRepository.deleteAllByAccountUsername(expected.getUsername())).thenReturn(1L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/" + expected.getUsername()))
                .andExpect(status().isNoContent());

        verify(accountRepository, times(1)).findByUsername(expected.getUsername());
        verify(accountRepository, times(1)).deleteByUsername(expected.getUsername());
        verifyNoMoreInteractions(accountRepository);

        verify(purityReportRepository, times(1)).deleteAllByAccountUsername(expected.getUsername());
        verifyNoMoreInteractions(purityReportRepository);
        verify(waterReportRepository, times(1)).deleteAllByAccountUsername(expected.getUsername());
        verifyNoMoreInteractions(waterReportRepository);
    }


    @Test
    public void deleteAccountByUsername_UsernameDoesNotExist_ReturnsNotFound() throws Exception {
        String username = "someUserThatDoesNotExist";

        when(accountRepository.findByUsername(username)).thenReturn(
                Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/" + username))
                .andExpect(status().isNotFound());

        verify(accountRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(accountRepository);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
