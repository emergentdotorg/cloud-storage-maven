package org.emergent.maven.cloud.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.AwsRegionProvider;
import java.util.Objects;
import java.util.function.Function;

public class AwsDefaultEnvRegionProvider extends AwsRegionProvider {

  private final Function<String, String> provider;

  public AwsDefaultEnvRegionProvider() {
    this(System::getenv);
  }

  public AwsDefaultEnvRegionProvider(Function<String, String> provider) {
    this.provider = Objects.requireNonNull(provider);
  }

  @Override
  public String getRegion() throws SdkClientException {
    return provider.apply("AWS_DEFAULT_REGION");
  }
}
