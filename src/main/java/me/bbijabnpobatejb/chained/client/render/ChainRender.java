package me.bbijabnpobatejb.chained.client.render;

import com.ibm.icu.impl.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class ChainRender {
    @Setter
    @Getter
    Set<Pair<Integer, Integer>> clientRenderChainedEntities = new HashSet<>();


}
