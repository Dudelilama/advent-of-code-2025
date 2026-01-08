package ch.zkb.advent.tresor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

class TresorServiceTest {

    TresorService tresorService = new TresorService();

    @ParameterizedTest
    @MethodSource("provideCreateMovements")
    void createMovements(int clicks, TresorService.Direction direction, int currentPosition, int expectedPosition, int expectedZeroCrosses) {
        TresorService.TresorMove tresorMove = tresorService.createTresorMove(new TresorService.Movement(direction, clicks), new AtomicInteger(currentPosition));

        Assertions.assertThat(tresorMove.currentPointer()).isEqualTo(expectedPosition);
        Assertions.assertThat(tresorMove.zeroCrossedCount()).isEqualTo(expectedZeroCrosses);
    }

    private static Stream<Arguments> provideCreateMovements() {
        return Stream.of(
                // simple ups
                Arguments.of(1, TresorService.Direction.R, 0, 1, 0),
                Arguments.of(50, TresorService.Direction.R, 0, 50, 0),
                Arguments.of(99, TresorService.Direction.R, 0, 99, 0),
                // simple downs
                Arguments.of(1, TresorService.Direction.L ,99, 98, 0),
                Arguments.of(50, TresorService.Direction.L, 99, 49, 0),
                Arguments.of(99, TresorService.Direction.L, 99, 0, 0),
                // flips by one click
                Arguments.of(1, TresorService.Direction.L, 0, 99, 1),
                Arguments.of(2, TresorService.Direction.L, 0, 98, 1),
                Arguments.of(1, TresorService.Direction.R, 0, 1, 0),
                Arguments.of(1, TresorService.Direction.R, 99, 0, 1),
                // flips
                Arguments.of(100, TresorService.Direction.L, 0, 0, 1),
                Arguments.of(200, TresorService.Direction.L, 0, 0, 2),
                Arguments.of(100, TresorService.Direction.L, 99, 99, 1),
                Arguments.of(200, TresorService.Direction.L, 99, 99, 2),
                Arguments.of(100, TresorService.Direction.R, 0, 0, 1),
                Arguments.of(200, TresorService.Direction.R, 0, 0, 2)
                );
    }
}