# marathon-consul

This is simple spring-boot application that consumes events emitted via the /v2/events service in Marathon and registers them as a service inside Consul. It supports docker containers that publish multiple ports.

The application is designed to be run from within Marathon so that it's lifecycle is tied to Marathon.

https://hub.docker.com/r/skytix/marathon-consul

## Requirements:

    Marathon 0.7 and above.

## Configuration

### Sample Marathon Config:

{ "args": [ "--zkHosts=:2181/marathon", "--excludedServiceNames=vault,someApp,someOtherApp" ], "container": { "type": "DOCKER", "docker": { "network": "HOST", "image": "skytix/marathon-consul:1.0.6", "forcePullImage": true } }, "id": "marathon-consul", "instances": 1, "cpus": 0.1, "mem": 256 }

Designed to try and drop in and discover everything, the only configuration you really need to provide is the zookeeper hosts.

Pass in the --zkHosts parameter to the container and it should discover your marathon masters.

Example, docker run -it skytix/marathon-consul --zkHosts=127.0.0.1:2181,192.168.1.100:2181/marathon

By default it will try and discover consul by trying to connect to 'consul.service.consul' assuming you have your DNS resolution sorted from within your containers. Otherwise, you can provide an alternative hostname to use to connect to consul.

Eg: docker run -it skytix/marathon-consul --consulAgent=192.168.1.101:8500

Applications that have multiple ports have -portX (where X is the port index) appended the service name.

For example, if your app in marathon, '/webapp', has 2 ports exposed, you would have the following services created in Consul:

webapp-port0 webapp-port1

If there is only a single port exposed, then there is no change to the app name.

