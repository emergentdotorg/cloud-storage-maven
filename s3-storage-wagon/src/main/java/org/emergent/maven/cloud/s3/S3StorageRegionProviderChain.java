package org.emergent.maven.cloud.s3;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.AwsEnvVarOverrideRegionProvider;
import com.amazonaws.regions.AwsProfileRegionProvider;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.AwsRegionProviderChain;
import com.amazonaws.regions.AwsSystemPropertyRegionProvider;
import com.amazonaws.regions.InstanceMetadataRegionProvider;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 */
public class S3StorageRegionProviderChain extends AwsRegionProviderChain {

    /**
     * Creates a region provider chain based on the default AWS region provider chain.
     */
    public S3StorageRegionProviderChain() {
        this(null);
    }

    /**
     * Creates a region provider chain based on the default AWS region provider chain.
     *
     * @param providedRegion may be null if default behavior is desired.
     */
    public S3StorageRegionProviderChain(String providedRegion) {
        this(providedRegion, Optional.empty(), Optional.empty());
    }

    /**
     * Useful for testing
     */
    S3StorageRegionProviderChain(String providedRegion,
                                 Function<String, String> envVarsProv,
                                 Function<String, String> sysPropProv) {
        this(providedRegion, Optional.of(envVarsProv), Optional.of(sysPropProv));
    }

    /**
     * Useful for testing
     */
    S3StorageRegionProviderChain(String providedRegion,
                                 Optional<Function<String, String>> envVarsProv,
                                 Optional<Function<String, String>> sysPropProv) {
        super(createProviderArray(providedRegion, new Backend(
            envVarsProv.orElse(System::getenv),
            sysPropProv.orElse(System::getProperty))));
    }

    private static AwsRegionProvider[] createProviderArray(String providedRegion, Backend backend) {
        Objects.requireNonNull(backend);

        return Stream.of(
            new MavenSettingsRegionProvider(providedRegion),
            new AwsDefaultEnvRegionProvider(backend::getenv),
            new AwsRegionProvider() {
                /**
                 * Adapted from {@link AwsEnvVarOverrideRegionProvider#getRegion()}
                 */
                @Override
                public String getRegion() throws SdkClientException {
                    return backend.getenv(SDKGlobalConfiguration.AWS_REGION_ENV_VAR);
                }
            },
            new AwsRegionProvider() {
                /**
                 * Adapted from {@link AwsSystemPropertyRegionProvider#getRegion()}
                 */
                @Override
                public String getRegion() throws SdkClientException {
                    return backend.getProperty(SDKGlobalConfiguration.AWS_REGION_SYSTEM_PROPERTY);
                }
            },
            new AwsProfileRegionProvider(),
            new InstanceMetadataRegionProvider()
        ).toArray(AwsRegionProvider[]::new);
    }

    private static class Backend {
        private final Function<String, String> envVarsProv;
        private final Function<String, String> sysPropProv;

        public Backend(Function<String, String> envVarsProv, Function<String, String> sysPropProv) {
            this.envVarsProv = Objects.requireNonNull(envVarsProv);
            this.sysPropProv = Objects.requireNonNull(sysPropProv);
        }

        public String getenv(String key) {
            return envVarsProv.apply(key);
        }
        public String getProperty(String key) {
            return sysPropProv.apply(key);
        }
    }
}
