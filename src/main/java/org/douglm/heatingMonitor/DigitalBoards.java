/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor;

import com.pi4j.context.Context;
import org.douglm.piSpi.PiSpi8DI;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mike Date: 3/27/25 Time: 22:50
 */
public class DigitalBoards implements AutoCloseable {
  public record DigitalBoard(DigitalBoardConfig digitalBoardConfig,
                             PiSpi8DI digitalBoard) {}
  private final List<DigitalBoard> digitalBoards = new ArrayList<>();

  public DigitalBoards(final Context pi4j,
                       final List<DigitalBoardConfig> digitalBoardsConfig) {
    for (final var digitalBoardConfig: digitalBoardsConfig) {
      digitalBoards.add(
              new DigitalBoard(digitalBoardConfig,
                               new PiSpi8DI(pi4j,
                                            digitalBoardConfig)));
    }
  }

  public List<DigitalBoard> getDigitalBoards() {
    return digitalBoards;
  }

  public void close() {
    for (final var digitalBoard: digitalBoards) {
      digitalBoard.digitalBoard().close();
    }
  }
}
