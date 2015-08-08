package com.synaptix.toast.runtime.core.parse;

import com.synaptix.toast.core.dao.IBlock;

import java.util.List;

/**
 * Created by Nico on 06/08/2015.
 */
public interface IBlockParser {
    IBlock digest(List<String> strings) throws Exception;
}
