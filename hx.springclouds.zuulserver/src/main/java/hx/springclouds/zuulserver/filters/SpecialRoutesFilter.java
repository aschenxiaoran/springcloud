package hx.springclouds.zuulserver.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import hx.springclouds.zuulserver.models.AbTestRoute;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SpecialRoutesFilter extends ZuulFilter {

    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private ProxyRequestHelper helper = new ProxyRequestHelper();
    private static final Logger logger=LoggerFactory.getLogger(SpecialRoutesFilter.class);

    @Autowired
    FilterUtils filterUtils;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public String filterType() {
        return filterUtils.ROUTE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext requestContext = RequestContext.getCurrentContext();
        AbTestRoute abTestRoute = getAbTestRoute(filterUtils.getServiceId());

        if (abTestRoute != null && isSpecialRouteUsed(abTestRoute)) {
            String requestURI = requestContext.getRequest().getRequestURI();
            String serviceName = requestContext.get("serviceId").toString();
            String route = buildRouteString(requestURI, abTestRoute.getEndpoint(), serviceName);
            forwordToSpecialRoute(route);
        }

        return null;
    }

    private AbTestRoute getAbTestRoute(String serviceName) {
        AbTestRoute abTestRoute = new AbTestRoute();
        abTestRoute.setActive("Y");
        abTestRoute.setServiceName("organizationservice");
        abTestRoute.setEndpoint("http://orgservice-new:8087");
        abTestRoute.setWeight(5);
        return abTestRoute;
    }

    private boolean isSpecialRouteUsed(AbTestRoute abTestRoute) {
        if (abTestRoute.getActive().equals("N")) {
            return false;
        }
        return true;
    }

    private String buildRouteString(String oldEndPoint, String newEndPoint, String serviceName) {
        int index = oldEndPoint.indexOf(serviceName);
        String strippedRoute = oldEndPoint.substring(index + serviceName.length());
        String route = String.format("%s/%s", newEndPoint, strippedRoute);
        return route;
    }

    private void forwordToSpecialRoute(String route) {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        MultiValueMap<String, String> headers = this.helper.buildZuulRequestHeaders(request);
        MultiValueMap<String, String> params = this.helper.buildZuulRequestQueryParams(request);

        String requestMethod = getRequestMethod(request);
        InputStream requestEntity = getRequestBody(request);
        if (request.getContentLength() < 0) {
            context.setChunkedRequestBody();
        }
        this.helper.addIgnoredHeaders();

        CloseableHttpClient httpClient = null;
        HttpResponse response = null;

        try {
            httpClient = HttpClients.createDefault();
            response = forward(httpClient, requestMethod, route, request, headers, params, requestEntity);
            setResponse(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException ex) {

            }
        }
    }

    private HttpResponse forward(CloseableHttpClient httpClient, String method, String uri,
                                 HttpServletRequest request,
                                 MultiValueMap<String, String> headers, MultiValueMap<String, String> params,
                                 InputStream requestEntity) throws Exception {
        URL host = new URL(uri);
        HttpHost httpHost = getHttpHost(host);
        HttpRequest httpRequest = null;
        InputStreamEntity httpEntity = getInputStreamEntity(requestEntity, request);

        switch (method) {
            case "POST":
                HttpPost httpPost = new HttpPost(uri);
                httpRequest = httpPost;
                httpPost.setEntity(httpEntity);
                break;
            case "PUT":
                HttpPut httpPut = new HttpPut(uri);
                httpRequest = httpPut;
                httpPut.setEntity(httpEntity);
                break;

            case "PATCH":
                HttpPatch httpPatch = new HttpPatch(uri);
                httpRequest = httpPatch;
                httpPatch.setEntity(httpEntity);
                break;

            default:
                httpRequest = new BasicHttpRequest(method, uri);
        }

        try {
            httpRequest.setHeaders(convertHeaders(headers));
            HttpResponse zuulResponse = forwordRequest(httpClient, httpHost, httpRequest);
            return zuulResponse;
        } finally {

        }

    }

    private HttpResponse forwordRequest(CloseableHttpClient httpClient, HttpHost httpHost, HttpRequest httpRequest)
            throws IOException {
        return httpClient.execute(httpHost, httpRequest);
    }

    private Header[] convertHeaders(MultiValueMap<String, String> headers) {
        List<Header> list = new ArrayList<>();
        for (String name : headers.keySet()) {
            for (String value : headers.get(name)) {
                list.add(new BasicHeader(name, value));
            }
        }
        return list.toArray(new BasicHeader[0]);
    }

    private InputStreamEntity getInputStreamEntity(InputStream requestEntity, HttpServletRequest request) {
        int contentLength = request.getContentLength();
        ContentType contentType = request.getContentType() != null ? ContentType.create(request.getContentType()) : null;
        InputStreamEntity entity = new InputStreamEntity(requestEntity, contentLength, contentType);
        return entity;
    }

    private HttpHost getHttpHost(URL host) {
        HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(), host.getProtocol());
        return httpHost;
    }

    private void setResponse(HttpResponse response) throws IOException {
        int status=response.getStatusLine().getStatusCode();
        InputStream entity=response.getEntity()!=null ? response.getEntity().getContent():null;
        MultiValueMap<String, String> headers=revertHeaders(response.getAllHeaders());
        this.helper.setResponse(status,entity,headers);
    }

    private MultiValueMap<String, String> revertHeaders(Header[] headers) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        for (Header header : headers) {
            String name = header.getName();
            if (!map.containsKey(name)) {
                map.put(name, new ArrayList<String>());
            }
            map.get(name).add(header.getValue());
        }
        return map;
    }

    private String getRequestMethod(HttpServletRequest request) {
        String method = request.getMethod();
        return method.toUpperCase();
    }

    private InputStream getRequestBody(HttpServletRequest request) {
        InputStream requestEntity = null;
        try {
            requestEntity = request.getInputStream();
        } catch (IOException ex) {

        }
        return requestEntity;
    }

}
