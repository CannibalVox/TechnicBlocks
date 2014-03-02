package net.technic.technicblocks.api;

import java.util.Collection;
import java.util.LinkedList;

public class TechnicBlocksApi {
    private static Collection<IFileProcessor> fileProcessors = new LinkedList<IFileProcessor>();

    public static void registerFileProcessor(IFileProcessor processor) {
        fileProcessors.add(processor);
    }

    public static void loadBlocks(String blockFile) {
        for(IFileProcessor processor : fileProcessors) {
            processor.processFile(blockFile);
        }
    }
}
