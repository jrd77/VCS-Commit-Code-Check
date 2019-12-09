import com.atzuche.order.config.CacheConfigTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/9 4:45 下午
 **/
@Resource
public class TestCacheConfig extends CacheConfigTemplate<List<String>> {
    @Override
    protected List<String> loadConfigData() {
        System.out.println("loadConfigData");
        List list = new ArrayList();
        list.add("1");
        list.add("2");
        return list;
    }

    @Override
    protected String key() {
        return "test";
    }


}
