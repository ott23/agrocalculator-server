package net.tngroup.acserver.controllers;

import net.tngroup.acserver.databases.cassandra.models.Client;
import net.tngroup.acserver.databases.cassandra.models.Geozone;
import net.tngroup.acserver.databases.cassandra.services.ClientService;
import net.tngroup.acserver.databases.cassandra.services.GeozoneService;
import net.tngroup.acserver.web.controllers.GeozoneController;
import net.tngroup.common.json.JsonComponent;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

import static net.tngroup.common.responses.Responses.failedDependencyResponse;
import static net.tngroup.common.responses.Responses.okResponse;
import static net.tngroup.common.responses.Responses.successResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class GeozoneControllerTests {

    private GeozoneController geozoneController;

    private ClientService clientService;
    private HttpServletRequest httpServletRequest;
    private GeozoneService geozoneService;


    @Before
    public void before() {

        clientService = mock(ClientService.class);
        httpServletRequest = mock(HttpServletRequest.class);
        geozoneService = mock(GeozoneService.class);

        geozoneController = new GeozoneController(clientService, geozoneService, new JsonComponent());
    }

    @Test
    public void givenValidRequest_whenCallGetList_thenShouldBeReturnList() {

        final List<Geozone> mockList = List.of();

        when(geozoneService.getAll()).thenReturn(mockList);

        assertEquals(
                geozoneController.getList(httpServletRequest),
                okResponse(mockList)
        );
    }

    @Test
    public void givenBadGeometry_whenCallSave_thenShouldBeTrowException() {

        final Geozone request = new Geozone();
        request.setGeometry("bad json ;;;;{}");

        try {
            geozoneController.save(httpServletRequest, request);
            fail();
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Json not valid");
            verifyZeroInteractions(clientService);
            verifyZeroInteractions(geozoneService);
        }
    }

    @Test
    public void givenUnknownClient_whenCallSave_thenShouldBeReturnFailedDependency() throws Exception {

        final Geozone request = new Geozone();

        when(clientService.getById(any())).thenReturn(null);

        assertEquals(
                geozoneController.save(httpServletRequest, request),
                failedDependencyResponse()
        );
        verifyZeroInteractions(geozoneService);
    }

    @Test
    public void givenValidGeozone_whenCallSave_thenShouldBeCallServiceAndReturnSuccess() throws Exception {

        final Geozone request = new Geozone();
        when(clientService.getById(any())).thenReturn(new Client());

        doAnswer(invocationOnMock -> {

            final Geozone geozone = invocationOnMock.getArgument(0);
            assertNotNull(geozone.getId());
            return null;
        }).when(geozoneService).save(any());

        assertEquals(
                geozoneController.save(httpServletRequest, request),
                successResponse()
        );

    }

    @Test
    public void givenRequest_whenCallDelete_thenShouldBeAlwaysSuccess(){

        assertEquals(
                successResponse(),
                geozoneController.deleteById(httpServletRequest, UUID.randomUUID())
        );

        verify(geozoneService, times(1)).deleteById(any());
    }

}
