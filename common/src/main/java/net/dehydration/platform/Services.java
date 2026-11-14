package net.dehydration.platform;

import java.util.ServiceLoader;

import net.dehydration.platform.services.IFluidHelper;
import net.dehydration.platform.services.INetworkHelper;
import net.dehydration.platform.services.IPlatformHelper;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static INetworkHelper network() {
        return NetworkHolder.NETWORK;
    }

    public static IFluidHelper fluid() {
        return FluidHolder.FLUID;
    }

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz, Services.class.getClassLoader()).findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }

    private static final class NetworkHolder {
        private static final INetworkHelper NETWORK = load(INetworkHelper.class);
    }

    private static final class FluidHolder {
        private static final IFluidHelper FLUID = load(IFluidHelper.class);
    }
}
