package com.company.resourceapi;

import com.company.resourceapi.controllers.ProjectRestController;
import com.company.resourceapi.repositories.ProjectRepository;
import com.company.resourceapi.repositories.SdlcSystemRepository;
import com.company.resourceapi.services.ProjectService;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProjectCodeApplication.class})
@WebAppConfiguration
class ProjectCodeApplicationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void contextLoads() {
    }

    // #################### Testing Post API's #######################
    @Test
    public void createProjectWithFullPayload() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v2/projects")
                .content("{\n\t\"externalId\": \"EXTERNALID\",\n\t\"name\": \"Name\",\n\t\"sdlcSystem\": {\n\t\t\"id\": 1\n\t}\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        String location = mvcResult.getResponse().getHeader("Location");
        Assert.assertTrue(location != null && location.contains("/api/v2/projects/"));
    }

    @Test
    public void createProjectWithMinimalPayload() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v2/projects")
                .content("{\n\t\"externalId\": \"EXTERNAL-ID\",\n\t\"sdlcSystem\": {\n\t\t\"id\": 1\n\t}\n}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        String location = mvcResult.getResponse().getHeader("Location");
        Assert.assertTrue(location != null && location.contains("/api/v2/projects/"));
    }

    @Test
    public void createProjectWithPayloadContainingIllegalValue() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v2/projects")
                .content("{\n\t\"sdlcSystem\": {\n\t\t\"id\": \"Whatever\"\n\t}\n}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createProjectWithPayloadNotContainingExternalId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v2/projects")
                .content("{\n\t\"sdlcSystem\": {\n\t\t\"id\": 1\n\t}\n}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createProjectWithPayloadNotContainingSystem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v2/projects")
                .content("{\n\t\"externalId\": \"EXTERNAL-ID\"\n}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createProjectWithPayloadContainingNonExistingSystem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v2/projects")
                .content("{\n\t\"externalId\": \"EXTERNALID\",\n\t\"sdlcSystem\": {\n\t\t\"id\": 12345\n\t}\n}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void createProjectWithPayloadContainingConflictingSystemAndExternalId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v2/projects")
                .content("{\n\t\"externalId\": \"SAMPLEPROJECT\",\n\t\"sdlcSystem\": {\n\t\t\"id\": 1\n\t}\n}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    //############ Testing Patch API's ##################

    @Test
    public void updateProjectWithFullPayload() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/5")
                .content("{\n\t\"externalId\": \"EXTERNALIDEDITED\",\n\t\"name\": \"Name-Edited\",\n\t\"sdlcSystem\": {\n\t\t\"id\": 1\n\t}\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        String response = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals("EXTERNALIDEDITED", JsonPath.parse(response).read("$.externalId"));
        Assert.assertEquals("Name-Edited", JsonPath.parse(response).read("$.name"));
        Assert.assertEquals(Integer.valueOf(1), JsonPath.parse(response).read("$.sdlcSystem.id"));
    }

    @Test
    public void updateProjectWithOnlyExternalId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/6")
                .content("{\n\t\"externalId\": \"EXTERNALIDEDITED\"\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals("EXTERNALIDEDITED", JsonPath.parse(response).read("$.externalId"));
        Assert.assertEquals("Project One", JsonPath.parse(response).read("$.name"));
        Assert.assertEquals(Integer.valueOf(3), JsonPath.parse(response).read("$.sdlcSystem.id"));

    }

    @Test
    public void updateProjectWithOnlySystemId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/7")
                .content("{\n\t\"sdlcSystem\": {\n\t\t\"id\": 1\n\t}\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals("PROJECTTWO", JsonPath.parse(response).read("$.externalId"));
        Assert.assertEquals("Project Two", JsonPath.parse(response).read("$.name"));
        Assert.assertEquals(Integer.valueOf(1), JsonPath.parse(response).read("$.sdlcSystem.id"));

    }

    @Test
    public void updateProjectEmptyPayload() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/8")
                .content("{\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals("PROJECTTHREE", JsonPath.parse(response).read("$.externalId"));
        Assert.assertEquals("Project Three", JsonPath.parse(response).read("$.name"));
        Assert.assertEquals(Integer.valueOf(3), JsonPath.parse(response).read("$.sdlcSystem.id"));


    }

    @Test
    public void updateProjectWithNullName() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/5")
                .content("{\n    \"name\": null\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Assert.assertNull( JsonPath.parse(response).read("$.name"));

    }

    @Test
    public void updateProjectPayloadContainingIllegalValues() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/1")
                .content("{\n\t\"sdlcSystem\": {\n\t\t\"id\": \"Whatever\"\n\t}\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateProjectPayloadContainingNonExistingSystem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/1")
                .content("{\n\t\"sdlcSystem\": {\n\t\t\"id\": 12345\n\t}\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateProjectPayloadContainingConflictingSystem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/1")
                .content("{\n\t\"sdlcSystem\": {\n\t\t\"id\": 2\n\t}\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void updateProjectPayloadContainingConflictingExternalId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/1")
                .content("{\n\t\"externalId\": \"PROJECTX\"\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void updateProjectPayloadContainingConflictingExternalIdAndSystem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/1")
                .content("{\n\t\"externalId\": \"PROJECTX\",\n\t\"sdlcSystem\": {\n\t\t\"id\": 2\n\t}\n}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void updateProjectWithIllegalPathVariable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/whatever")
                .content("{}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateProjectWithInvalidPathVariable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v2/projects/1234")
                .content("{}")
                .characterEncoding("utf8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}
