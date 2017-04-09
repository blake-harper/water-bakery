package com.onewheelwizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onewheelwizard.bakery.BakeryApplication;
import com.onewheelwizard.bakery.data.AccountRepository;
import com.onewheelwizard.bakery.data.PurityReportRepository;
import com.onewheelwizard.bakery.data.WaterReportRepository;
import com.onewheelwizard.bakery.model.Account;
import com.onewheelwizard.bakery.model.PurityReport;
import com.onewheelwizard.bakery.model.WaterReport;
import com.onewheelwizard.bakery.model.constants.UserType;
import com.onewheelwizard.bakery.model.constants.WaterCondition;
import com.onewheelwizard.bakery.model.constants.WaterPurityCondition;
import com.onewheelwizard.bakery.model.constants.WaterType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BakeryApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class PurityReportRestControllerTest {
    // section: server setup / mocking
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));


    // section: repositories
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WaterReportRepository waterReportRepository;
    @Autowired
    private PurityReportRepository purityReportRepository;

    private static final String username = "unitTest123";

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Account account;
    private PurityReport purityReport;

    // section: json setup/handling
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @SuppressWarnings("unchecked")
        // object will be something Jackson can handle, or we should throw because something major is wrong
    String json(Object object) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                object, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    // section: test setup

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.purityReportRepository.deleteAllInBatch();
        this.waterReportRepository.deleteAllInBatch();
        this.accountRepository.deleteAllInBatch();

        this.account = accountRepository
                .save(new Account(username, "password", UserType.MANAGER, "email@email.email", "Sir", "Citytown"));
        this.purityReport = purityReportRepository.save(
                new PurityReport(account, ZonedDateTime.now(), 50.5, 40.4, WaterPurityCondition.UNSAFE, 1.2f, 2.3f));
    }

    // section: unit tests
    @Test
    public void postWaterReport_UserDoesNotExist_ReturnsIsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/larry/purity-reports/")
                .content(json(new WaterReport(null, null, 0, 0, WaterType.OTHER, WaterCondition.TREATABLE_MUDDY)))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postWaterReport_ValidReport_ReturnsIsOkAndValidReport() throws Exception {
        double latitude = 13.37;
        double longitude = 26.74;
        WaterPurityCondition expectedWaterPurityCondition = WaterPurityCondition.TREATABLE;
        float virusPpm = 0.37f;
        float contaminantPpm = 0.74f;

        mockMvc.perform(MockMvcRequestBuilders.post("/" + username + "/purity-reports/")
                .content(json(new PurityReport(null, null, latitude, longitude, expectedWaterPurityCondition,
                        virusPpm, contaminantPpm)))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorUsername").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.postDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.latitude").value(latitude))
                .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(longitude))
                .andExpect(MockMvcResultMatchers.jsonPath("$.waterPurityCondition").value(expectedWaterPurityCondition.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.virusPpm").value(virusPpm))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contaminantPpm").value(contaminantPpm));
    }

    @Test
    public void getWaterReportById_ReportDoesNotExist_ReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/" + username + "/purity-reports/" + (purityReport.getId() + 100)))
                .andExpect(status().isNotFound());
    }
}
