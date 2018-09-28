package net.tngroup.acserver.controllers;

import net.tngroup.acserver.databases.cassandra.models.Client;
import net.tngroup.acserver.databases.cassandra.models.Unit;
import net.tngroup.acserver.databases.cassandra.services.ClientService;
import net.tngroup.acserver.databases.cassandra.services.UnitService;
import net.tngroup.acserver.web.controllers.UnitController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

import static net.tngroup.common.responses.Responses.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UnitControllerTests {

    private UnitController unitController;

    private UnitService unitService;
    private ClientService clientService;
    private HttpServletRequest httpServletRequest;


    @Before
    public void before(){

        unitService = mock(UnitService.class);
        clientService = mock(ClientService.class);
        httpServletRequest = mock(HttpServletRequest.class);

        unitController = new UnitController(unitService, clientService);
    }

    @Test
    public void givenValidRequest_whenCallGetList_thenShouldBeReturnList(){

        final List<Unit> mockList = List.of();

        when(unitService.getAll()).thenReturn(mockList);

        assertEquals(
                unitController.getList(httpServletRequest),
                okResponse(mockList)
        );
    }

    @Test
    public void givenValidUnitId_whenCallSave_thenReturnSuccess(){

        when(unitService.getAllByImei(any())).thenReturn(List.of());
        when(clientService.getById(any())).thenReturn(new Client());

        doAnswer(invocationOnMock -> {

            final Unit unit = invocationOnMock.getArgument(0);
            assertNotNull(unit.getId());
            return null;
        }).when(unitService).save(any());

        assertEquals(
                unitController.save(httpServletRequest, new Unit()),
                successResponse()
        );
    }

    @Test
    public void givenExistUnit_whenCallSave_thenShouldBeReturnConflictResponse(){

        final Unit request = new Unit();
        request.setId(UUID.randomUUID());

        final Unit existUnit = new Unit();
        existUnit.setId(UUID.randomUUID());

        when(unitService.getAllByImei(any())).thenReturn(List.of(existUnit));

        assertEquals(
                conflictResponse("imei"),
                unitController.save(httpServletRequest, request)

        );

        verifyZeroInteractions(clientService);
        verify(unitService, times(0)).save(any());
    }

    @Test
    public void givenUnknownClient_whenCallSave_thenShouldBeReturnFailedDependency(){

        final UUID unitId = UUID.randomUUID();
        final Unit request = new Unit();
        request.setId(unitId);

        final Unit existUnit = new Unit();
        existUnit.setId(unitId);

        when(unitService.getAllByImei(any())).thenReturn(List.of(existUnit));
        when(clientService.getById(any())).thenReturn(null);

        assertEquals(
                failedDependencyResponse(),
                unitController.save(httpServletRequest, request)
        );
        verify(unitService, times(0)).save(any());

    }

    @Test
    public void givenValidRequest_whenCallSave_thenShouldBeSaveUnitAndReturnSuccess(){

        final UUID unitId = UUID.randomUUID();
        final Unit request = new Unit();
        request.setId(unitId);

        final Unit existUnit = new Unit();
        existUnit.setId(unitId);

        when(unitService.getAllByImei(any())).thenReturn(List.of(existUnit));
        when(clientService.getById(any())).thenReturn(new Client());

        assertEquals(
                successResponse(),
                unitController.save(httpServletRequest, request)
        );
        verify(unitService, times(1)).save(any());
    }

    @Test
    public void givenRequest_whenCallDelete_thenShouldBeAlwaysSuccess(){

        assertEquals(
                successResponse(),
                unitController.deleteById(httpServletRequest, UUID.randomUUID())
        );

        verify(unitService, times(1)).deleteById(any());
    }


}
