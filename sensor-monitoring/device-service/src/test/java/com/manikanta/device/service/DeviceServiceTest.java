package com.manikanta.device.service;

import com.manikanta.common.exception.DuplicateResourceException;
import com.manikanta.common.exception.ResourceNotFoundException;
import com.manikanta.device.dto.DeviceRequest;
import com.manikanta.device.dto.DeviceResponse;
import com.manikanta.device.model.Device;
import com.manikanta.device.model.DeviceStatus;
import com.manikanta.device.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    void testCreateDeviceSuccess() {
        DeviceRequest request = new DeviceRequest();
        request.setDeviceCode("D1");
        request.setName("Device 1");
        request.setLocation("Loc 1");
        request.setStatus(DeviceStatus.ACTIVE);

        when(deviceRepository.existsByDeviceCode("D1")).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> {
            Device d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });

        DeviceResponse response = deviceService.createDevice(request);

        assertNotNull(response);
        assertEquals("D1", response.getDeviceCode());
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void testCreateDeviceDuplicate() {
        DeviceRequest request = new DeviceRequest();
        request.setDeviceCode("D1");

        when(deviceRepository.existsByDeviceCode("D1")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> deviceService.createDevice(request));
    }

    @Test
    void testGetDeviceByIdSuccess() {
        Device device = Device.builder().id(1L).deviceCode("D1").build();
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        DeviceResponse response = deviceService.getDeviceById(1L);

        assertEquals("D1", response.getDeviceCode());
    }

    @Test
    void testGetDeviceByIdNotFound() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deviceService.getDeviceById(1L));
    }
}
