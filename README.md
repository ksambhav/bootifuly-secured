# bootifuly-secured

Reference implementation of typical Single Sign On (SSO) scenario using Spring Boot, AngularJS 1.x, Angular Material.  External providers can be configured (Google implementation already done). This approach can also be used when the sso app needs to communicate with various REST based microservices implemented using Spring Boot.

## Installation

mvn clean install

## Usage

Auth server (uaa) mvn spring-boot:run
SSO app     (sso) mvn spring-boot-run

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D


## Credits

### https://spring.io/guides/tutorials/spring-boot-oauth2

