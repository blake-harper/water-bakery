package com.onewheelwizard.integration;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BakeryApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class PurityReportRestControllerIntegrationTest {
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
    private List<PurityReport> purityReports;

    private Random r;

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

        this.r = new Random(System.currentTimeMillis());

        this.account = accountRepository
                .save(new Account(username, "password", UserType.MANAGER, "email@email.email", "Sir", "Citytown"));

        this.purityReports = new LinkedList<>();

        this.purityReports.add(purityReportRepository.save(generateRandomReport(this.account)));
        this.purityReports.add(purityReportRepository.save(generateRandomReport(this.account)));
        this.purityReports.add(purityReportRepository.save(generateRandomReport(this.account)));
    }

    private PurityReport generateRandomReport(Account account) {
        return new PurityReport(account, ZonedDateTime.now(), r.nextDouble(), r.nextDouble(),
                WaterPurityCondition.values()[r.nextInt(WaterPurityCondition.values().length)], r.nextFloat(), r.nextFloat());
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
        PurityReport expectedReport = generateRandomReport(this.account);

        mockMvc.perform(MockMvcRequestBuilders.post("/" + username + "/purity-reports/")
                .content(json(expectedReport))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorUsername").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.postDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.latitude").value(expectedReport.getLatitude()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(expectedReport.getLongitude()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.waterPurityCondition").value(expectedReport.getWaterPurityCondition().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.virusPpm").value(expectedReport.getVirusPpm()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contaminantPpm").value(expectedReport.getContaminantPpm()));
    }

    @Test
    public void getUserWaterReportById_ReportDoesNotExist_ReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/" + username + "/purity-reports/" + (purityReports.get(0).getId() + 100)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getWaterReportById_ReportDoesNotExist_ReturnsNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/purity-reports/" + (purityReports.get(0).getId() + 100)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getWaterReportById_ValidReportId_ReturnsReport() throws Exception {
        PurityReport expectedReport = purityReports.get(1);


        mockMvc.perform(MockMvcRequestBuilders.get("/purity-reports/" + expectedReport.getId())
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorUsername").value(username))
                .andExpect(MockMvcResultMatchers.jsonPath("$.postDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.latitude").value(expectedReport.getLatitude()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(expectedReport.getLongitude()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.waterPurityCondition").value(expectedReport.getWaterPurityCondition().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.virusPpm").value(expectedReport.getVirusPpm()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contaminantPpm").value(expectedReport.getContaminantPpm()));
    }
}
