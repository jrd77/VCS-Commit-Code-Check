package com.github.jrd77.codecheck.data.persistent.convert;

import com.github.jrd77.codecheck.data.model.FileMatchModel;

/**
 * @author zhen.wang
 * @description 文件名规则转换器
 * @date 2021/8/25 10:31
 */
public class FileMatchModelConverter extends AbstractEntityJsonConverter<FileMatchModel> {
    @Override
    Class<FileMatchModel> getGenericClass() {
        return FileMatchModel.class;
    }


//    @Override
//    public @Nullable FileMatchModel fromString(@NotNull String s) {
//        JSONParser jsonParser=new JSONParser();
//        FileMatchModel model=new FileMatchModel();
//        try {
//            Object parse = jsonParser.parse(s);
//            model = ReflectUtil.mapToEntity((JSONObject) parse, FileMatchModel.class);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return model;
//    }
//
//    @Override
//    public @Nullable String toString(@NotNull FileMatchModel codeMatchModel) {
//        JSONObject jsonObject = new JSONObject();
//        Map<String, Object> map = ReflectUtil.entityToMap(codeMatchModel, false);
//        for (String s : map.keySet()) {
//            jsonObject.put(s,map.get(s));
//        }
//        return jsonObject.toJSONString();
//    }
}
