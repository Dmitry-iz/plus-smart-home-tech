//package ru.yandex.practicum.grpc;
//
//import io.grpc.Status;
//import io.grpc.StatusRuntimeException;
//import io.grpc.stub.StreamObserver;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.devh.boot.grpc.server.service.GrpcService;
//import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
//import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
//import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
//import ru.yandex.practicum.model.HubEvent;
//import ru.yandex.practicum.model.SensorEvent;
//import ru.yandex.practicum.service.EventMapperService;
//import ru.yandex.practicum.service.KafkaProducerService;
//import com.google.protobuf.Empty;
//
//@Slf4j
//@GrpcService
//@RequiredArgsConstructor
//public class CollectorGrpcService extends CollectorControllerGrpc.CollectorControllerImplBase {
//
//    private final KafkaProducerService kafkaProducerService;
//    private final EventMapperService eventMapperService;
//    private final GrpcToModelMapper grpcToModelMapper;
//
//    @Override
//    public void sendSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
//        try {
//            log.info("Received sensor event via gRPC: {}", request.getId());
//
//            SensorEvent sensorEvent = grpcToModelMapper.toSensorEvent(request);
//
//            kafkaProducerService.sendSensorEvent(sensorEvent);
//
//            log.debug("Sensor event processed successfully via gRPC");
//            responseObserver.onNext(Empty.getDefaultInstance());
//            responseObserver.onCompleted();
//        } catch (Exception e) {
//            log.error("Failed to process sensor event via gRPC: {}", request, e);
//            responseObserver.onError(new StatusRuntimeException(
//                    Status.INTERNAL
//                            .withDescription(e.getLocalizedMessage())
//                            .withCause(e)
//            ));
//        }
//    }
//
//    @Override
//    public void sendHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
//        try {
//            log.info("Received hub event via gRPC: {}", request.getHubId());
//
//            HubEvent hubEvent = grpcToModelMapper.toHubEvent(request);
//
//            kafkaProducerService.sendHubEvent(hubEvent);
//
//            log.debug("Hub event processed successfully via gRPC");
//            responseObserver.onNext(Empty.getDefaultInstance());
//            responseObserver.onCompleted();
//        } catch (Exception e) {
//            log.error("Failed to process hub event via gRPC: {}", request, e);
//            responseObserver.onError(new StatusRuntimeException(
//                    Status.INTERNAL
//                            .withDescription(e.getLocalizedMessage())
//                            .withCause(e)
//            ));
//        }
//    }
//}

package ru.yandex.practicum.grpc;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.SensorEventHandler;
import com.google.protobuf.Empty;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CollectorGrpcService extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final GrpcToModelMapper grpcToModelMapper;

    public CollectorGrpcService(Set<SensorEventHandler> sensorEventHandlers, GrpcToModelMapper grpcToModelMapper) {
        this.grpcToModelMapper = grpcToModelMapper;
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getMessageType,
                        handler -> handler
                ));
    }

    @Override
    public void sendSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received sensor event via gRPC: {}", request.getId());

            // Используем обработчики как в задании
            if (sensorEventHandlers.containsKey(request.getPayloadCase())) {
                sensorEventHandlers.get(request.getPayloadCase()).handle(request);
            } else {
                throw new IllegalArgumentException("Не могу найти обработчик для события " + request.getPayloadCase());
            }

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Failed to process sensor event via gRPC: {}", request, e);
            responseObserver.onError(new StatusRuntimeException(io.grpc.Status.fromThrowable(e)));
        }
    }

    @Override
    public void sendHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received hub event via gRPC: {}", request.getHubId());

            // Для hub events пока используем старый подход
            var hubEvent = grpcToModelMapper.toHubEvent(request);
            // Здесь нужно будет добавить HubEventHandlers аналогично SensorEventHandlers

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Failed to process hub event via gRPC: {}", request, e);
            responseObserver.onError(new StatusRuntimeException(io.grpc.Status.fromThrowable(e)));
        }
    }
}