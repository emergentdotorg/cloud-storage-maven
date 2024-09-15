# GoogleStorageWagon

The GoogleStorageWagon project enables you to upload your artifacts to a Google Cloud Storage bucket.

## Maven Configuration

First add the `google-storage-wagon` extension to your pom.xml:
```xml
<build>
    <extensions>
        <extension>
            <groupId>org.emergent.maven.cloud</groupId>
            <artifactId>google-storage-wagon</artifactId>
            <version>3.0</version>
        </extension>
    </extensions>
</build>
```

Second, add your repositories with 'gs' as the protocol and the bucket name in place of the hostname in the URL:
```xml
<repositories>
    <repository>
        <id>my-repo-bucket-release</id>
        <url>gs://mavenrepository/release</url>
        <releases>
            <enabled>true</enabled>
            <checksumPolicy>warn</checksumPolicy>
        </releases>
        <snapshots>
            <enabled>false</enabled>
            <checksumPolicy>warn</checksumPolicy>
        </snapshots>
    </repository>
    <repository>
        <id>my-repo-bucket-snapshot</id>
        <url>gs://mavenrepository/snapshot</url>
        <releases>
            <enabled>false</enabled>
            <checksumPolicy>warn</checksumPolicy>
        </releases>
        <snapshots>
            <enabled>true</enabled>
            <checksumPolicy>warn</checksumPolicy>
        </snapshots>
    </repository>
</repositories>
```

Finally, optionally add the repositories to 'distributionManagement', this enables artifact uploads via maven 'deploy':
```xml
<distributionManagement>
    <repository>
        <id>my-repo-bucket-release</id>
        <url>gs://mavenrepository/release</url>
    </repository>
    <snapshotRepository>
        <id>my-repo-bucket-snapshot</id>
        <url>gs://mavenrepository/snapshot</url>
    </snapshotRepository>
</distributionManagement>
```

## Authentication

The `google-storage-wagon` uses an authentication library that looks for
[GCS Application Default Credentials](https://cloud.google.com/docs/authentication/application-default-credentials).
Setting the `GOOGLE_APPLICATION_CREDENTIALS` environment variable, for example, is known to work.

Full guide on https://egkatzioura.com/2018/04/09/host-your-maven-artifacts-using-google-cloud-storage/
