package net.tngroup.acserver.controllers;

import net.tngroup.acserver.databases.h2.models.Node;
import net.tngroup.acserver.databases.h2.models.Setting;
import net.tngroup.acserver.databases.h2.services.SettingService;
import net.tngroup.acserver.web.controllers.SettingController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static net.tngroup.common.responses.Responses.okFullResponse;
import static net.tngroup.common.responses.Responses.okResponse;
import static net.tngroup.common.responses.Responses.successResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


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
    public void givenValidRequest_whenCallGetList_thenShouldBeReturnOkFullResponse() {

        final List<Setting> mockResult = new ArrayList<>();
        when(settingService.getAll()).thenReturn(mockResult);
        assertEquals(
                settingController.getList(httpServletRequest),
                okFullResponse(mockResult)
        );
    }

    @Test
    public void givenValidRequest_whenCallGetListByCalculatorId_thenShouldBeReturnOkResponse() {

        final List<Setting> mockResult = new ArrayList<>();
        final int mockId = 0;
        when(settingService.getAllByCalculatorId(mockId)).thenReturn(mockResult);
        assertEquals(
                settingController.getListByCalculatorId(httpServletRequest, mockId),
                okResponse(mockResult)
        );
    }

    @Test
    public void givenNewSetting_whenCallSetForCalculator_thenShouldBeSaveNewSettingAndReturnSuccess() {

        when(settingService.getByNameAndCalculatorId(anyString(), anyInt())).thenReturn(null);

        final Setting inputSetting = new Setting();
        inputSetting.setNode(new Node());
        inputSetting.setName("");
        inputSetting.setValue("");
        final ResponseEntity result = settingController.setForCalculator(httpServletRequest, inputSetting);

        verify(settingService, times(1)).save(any(Setting.class));

        assertEquals(result, successResponse());
    }

    //TODO исправить тест после фикса метода
    /*@Test
    public void givenOldSetting_whenCallSetForCalculator_thenShouldBeSaveUpdatedSettingAndReturnSuccess(){

        when(settingService.getByNameAndCalculatorId(anyString(), anyInt())).thenReturn(null);

        final ResponseEntity result = settingController.setForCalculator(httpServletRequest, new Setting());

        verify(settingService, times(1)).save(any(Setting.class));

        assertEquals(result, successResponse());
    }*/

    @Test
    public void givenSetting_whenCallSet_thenShouldBeSaveSettingAndReturnSuccess() {

        final Setting setting = new Setting();
        assertEquals(settingController.set(httpServletRequest, setting), successResponse());
        verify(settingService, times(1)).save(setting);
    }

    @Test
    public void givenExistSetting_whenCallDeleteById_thenReturnSuccess() throws Exception {

        final int mockId = 0;
        final Setting existSetting = new Setting();
        existSetting.setNode(new Node());
        when(settingService.getById(anyInt())).thenReturn(existSetting);

        assertEquals(
                settingController.deleteById(httpServletRequest, mockId),
                successResponse()
        );

        verify(settingService, times(1)).deleteById(mockId);
    }

    @Test
    public void givenNonExistSetting_whenCallDeleteById_thenReturnSuccess(){

        when(settingService.getById(anyInt())).thenReturn(null);

        try{
            settingController.deleteById(httpServletRequest, 0);
        } catch (Exception e){
            return;
        }
        fail();
    }

}
