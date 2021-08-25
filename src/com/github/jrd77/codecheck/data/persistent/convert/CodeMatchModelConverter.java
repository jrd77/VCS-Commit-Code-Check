package com.github.jrd77.codecheck.data.persistent.convert;

import com.github.jrd77.codecheck.data.model.CodeMatchModel;

/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/8/25 10:31
 */
public class CodeMatchModelConverter extends AbstractEntityJsonConverter<CodeMatchModel> {
    @Override
    Class<CodeMatchModel> getGenericClass() {
        return CodeMatchModel.class;
    }

//    @Override
//    public @Nullable CodeMatchModel fromString(@NotNull String s) {
//        JSONParser jsonParser=new JSONParser();
//        CodeMatchModel model=new CodeMatchModel();
//        try {
//            Object parse = jsonParser.parse(s);
//            model = ReflectUtil.mapToEntity((JSONObject) parse, CodeMatchModel.class);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return model;
//    }
//
//    @Override
//    public @Nullable String toString(@NotNull CodeMatchModel codeMatchModel) {
//        JSONObject jsonObject = new JSONObject();
//        Map<String, Object> map = ReflectUtil.entityToMap(codeMatchModel, false);
//        for (String s : map.keySet()) {
//            jsonObject.put(s,map.get(s));
//        }
//        return jsonObject.toJSONString();
//    }
}
