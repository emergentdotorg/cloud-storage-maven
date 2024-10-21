package org.emergent.maven.cloud.s3;

import static com.amazonaws.SDKGlobalConfiguration.AWS_REGION_ENV_VAR;
import static com.amazonaws.SDKGlobalConfiguration.AWS_REGION_SYSTEM_PROPERTY;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;

public class S3StorageRegionProviderChainTest {

  private static final String PROVIDED_REGION = "provided-region";
  private static final String ENV_VAR_REGION = "env-var-region";
  private static final String SYSTEM_PROPERTY_REGION = "sys-prop-region";

  private final AtomicReference<Function<String, String>> envVarsProvRef = new AtomicReference<>();
  private final AtomicReference<Function<String, String>> sysPropProvRef = new AtomicReference<>();

  private final ChainFactory factory =
      reg ->
          new S3StorageRegionProviderChain(
              reg,
              k -> Optional.ofNullable(envVarsProvRef.get()).orElse(System::getenv).apply(k),
              k -> Optional.ofNullable(sysPropProvRef.get()).orElse(System::getProperty).apply(k));

  @Test
  public void testProvidedRegionConstructor() {
    final S3StorageRegionProviderChain regionProvider = factory.getInstance(PROVIDED_REGION);
    Assert.assertEquals(PROVIDED_REGION, regionProvider.getRegion());
  }

  @Test
  public void testEnvVarRegion() {
    envVarsProvRef.set(getOrDef(AWS_REGION_ENV_VAR, ENV_VAR_REGION, System::getenv));
    final S3StorageRegionProviderChain regionProvider = factory.getInstance();
    Assert.assertEquals(ENV_VAR_REGION, regionProvider.getRegion());
  }

  @Test
  public void testDefaultEnvVarRegion() {
    envVarsProvRef.set(getOrDef("AWS_DEFAULT_REGION", ENV_VAR_REGION, System::getenv));
    final S3StorageRegionProviderChain regionProvider = factory.getInstance();
    Assert.assertEquals(ENV_VAR_REGION, regionProvider.getRegion());
  }

  @Test
  public void testSystemPropertyRegion() {
    sysPropProvRef.set(
        getOrDef(AWS_REGION_SYSTEM_PROPERTY, SYSTEM_PROPERTY_REGION, System::getProperty));
    final S3StorageRegionProviderChain regionProvider = factory.getInstance();
    Assert.assertEquals(SYSTEM_PROPERTY_REGION, regionProvider.getRegion());
  }

  private static Function<String, String> getOrDef(String k, String v, Function<String, String> f) {
    Map<String, String> map = new HashMap<>();
    map.put(k, v);
    return getOrDef(map, f);
  }

  private static Function<String, String> getOrDef(
      Map<String, String> map, Function<String, String> f) {
    return k -> map.containsKey(k) ? map.get(k) : f.apply(k);
  }

  public interface ChainFactory {

    default S3StorageRegionProviderChain getInstance() {
      return getInstance(null);
    }
    ;

    S3StorageRegionProviderChain getInstance(String providedRegion);
  }
}
