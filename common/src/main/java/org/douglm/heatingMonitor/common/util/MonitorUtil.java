/* ********************************************************************
    Appropriate copyright notice
*/
package org.douglm.heatingMonitor.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.douglm.heatingMonitor.common.MonitorException;

import java.io.File;
import java.io.IOException;

/**
 * User: mike Date: 12/14/25 Time: 18:46
 */
public class MonitorUtil {
  final static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  public static  <T> T readConfig(final String fileName,
                                  final Class<T> clazz) {
    final var classLoader = Thread.currentThread().getContextClassLoader();

    final var url = classLoader.getResource(fileName);
    if (url == null) {
      throw new MonitorException("Unable to locate resource " + fileName);
    }
    final var file = new File(url.getFile());
    try {
      return mapper.readValue(file, clazz);
    } catch (final IOException ioe) {
      throw new MonitorException(ioe);
    }
  }
}
