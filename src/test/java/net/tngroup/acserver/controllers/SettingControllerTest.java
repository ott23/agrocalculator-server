package net.tngroup.acserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.tngroup.acserver.databases.h2.models.Setting;
import net.tngroup.acserver.databases.h2.services.SettingService;
import net.tngroup.acserver.web.controllers.SettingController;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static net.tngroup.common.responses.Responses.okFullResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SettingControllerTest {

    private SettingController settingController;
    private SettingService settingService;
    private HttpServletRequest httpServletRequest;


    @Before
    public void initMocks() {

        settingService = mock(SettingService.class);
        httpServletRequest = mock(HttpServletRequest.class);
        settingController = new SettingController(settingService);
    }

    @Test
    public void givenValidRequest_whenCallGetList_thenReturnOkFullResponse() throws JsonProcessingException {

        final List<Setting> mockResult = new ArrayList<>();
        when(settingService.getAll()).thenReturn(mockResult);
        assertEquals(
                settingController.getList(httpServletRequest),
                okFullResponse(mockResult)
        );
    }
}
