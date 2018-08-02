package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectClient {

    private ConcurrentMap<Long, ProjectInfo> concurrentHashMap;

    private final RestOperations restOperations;
    private final String endpoint;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
        concurrentHashMap = new ConcurrentHashMap();
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo forObject = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        concurrentHashMap.put(projectId, forObject);
        return forObject;
    }

    public ProjectInfo getProjectFromCache(long projectId) {

        return concurrentHashMap.get(projectId);
    }
}
