package cn.race.teacher.service;

import cn.race.teacher.pojo.TsAns;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2022-03-30
 */
public interface ITsAnsService extends IService<TsAns> {
int addTsAns(Integer pubId, Integer stId,String name);
TsAns selectById(Integer tsAnsId);
int updateTsAns(Integer tsAnsId,TsAns tsAns);
}
