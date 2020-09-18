package com.uit.cloud.kubernetes;

/*
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */

import com.github.kubesys.kubernetes.ExtendedKubernetesClient;
import com.github.kubesys.kubernetes.api.model.*;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;

import java.util.*;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @author wuyuewen@otcaix.iscas.ac.cn
 * @author liuhe@otcaix.iscas.ac.cn
 *
 * @version 1.3.0
 * @since   2019/9/3
 *
 */
public class AbstractTest {

    public static Config config = new ConfigBuilder()
            .withApiVersion("v1")
            .withCaCertData("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUN5RENDQWJDZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwcmRXSmwKY201bGRHVnpNQjRYRFRJd01ESXhNVEV5TWpVMU5Gb1hEVE13TURJd09ERXlNalUxTkZvd0ZURVRNQkVHQTFVRQpBeE1LYTNWaVpYSnVaWFJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBT3VSClQ2MDM4SWEyMmJwdkIxbGRlcWkwZzN4TFFIUSs3Yk5JV3dkY1N1TklxeXh2TjlqM3ZqUnZZNW1WN1hPSTh3NkwKL2VSclhTeUpvRTRzVUhCclFEM0RPSW5wZFZFUlJoOUJOOXRHMG9ZZnI3V0ZvRUx3VHZTT1ZWcDdVVmdhSWF5UQpDYXlpRldFTXFCem1jUVI1VjN4amFjMlZaaFlXQ1hHUHdKWDBYQUQvRXRqYkVUMUc5VldTVE44Vm91ODl5emVMCnlNTjB6VC82SG1GLzFORjRpYm1QdVkwbmRFSkppYy9UMTFrUXFuN1RWMlcxRWZXVXhZTHBReFAvbWVnenltb2IKUUZKVmlwdUVXSy9wMDJ6a0NlcGw1bVpXN25iOUFZYjg1ZC9vRDRSOVNGNElNUmdxT2VHR2tCQVp0Smo4eUJNeQpzUDI3akJHbFFPaCtVTkdvcmxFQ0F3RUFBYU1qTUNFd0RnWURWUjBQQVFIL0JBUURBZ0trTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFHUkpGRWRJcCs0SUZZbUkrR0czM3dvblVQOWoKTllsVDAxN2dyL01FMHZRRnpYZE1qckp0MlhQbFh4aFNUZWRBTFQydWpUeGhWcVRLWGhCdlY4Y20yUUxWUWpVVwp2Ykk1SlBQU3NZdFlBS0c5NGt3K0ZxSS9GUXJFYUdxbEVZZTVmdlgyOEFmQ0ZtWWJyVUpVSzRvaE11eU5rUm4wCkRKMk1uL3RCZjNsQU1YNXQ5OXBFQzRIRDBCUGFRWEpwekwxVTFpa0dUMStWdkVGYkY2UFB0Qzc5VmVPVk9tb1EKTlVTc0NIRmZCZ0c3WjBzcmNOVUF3VW03di8vMTdpY05JRWx1bk5JS2d3bjNOaFZTeVhaWGpkKzkybm9hUGJFawo4WXBCd3JWemlvci83dy9hWEJqYTI2bHNlZWV3Z3dIOHZPdDl3UDJ6a3hHdWUzT2VuV0lreXZ0NGtnMD0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=")
            .withClientCertData("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUM4akNDQWRxZ0F3SUJBZ0lJQ0FxNFZPSEg5VTB3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TURBeU1URXhNakkxTlRSYUZ3MHlNVEF5TVRBeE1qSTFOVGxhTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQTMyRlF1KzV3UVUzaThGL04KblRSQ1NOaS9jbGZoR1YyaDY0amhLWTZXWmZFNjUwRjhhRjNNMElzZDhmY09FdkRvVDEwSVp1VVNtVDlIRXVlbAplb0dTcExEQURWTVR6Y3dnUzQxcE5wQlZjVlpQQUxzZzJVYjNGY1ZiTmZFOGV5VHFNS2VZRGZoak0vZitjcjk2ClduMVFORFJScmlPRjFHUDZiWXlRQVVKWE5JUTUxVGNiYUtFdEp3b3ZLYTE0amVKVmxFMklIT2wvb3Y0L2FUWmMKWEdrSkhzdEp1YThUcnVCOU53TG5PVStjSndkRVdhcVhWdkNBQU1OQnJhZ3k5YU9zQ1k1T3RqOXhpTkEwRHF6eApCZU4vREpPdHoxN0dTdS9OR1Y2WHpIWkE4bm15clpoS2FCV1hYZmxnNEtWbDE3bVh5a1IyemdHMEgzYXlUY1lBCmk4VGQ0UUlEQVFBQm95Y3dKVEFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFHcjYxVTZBdXpMdDJXMWhxUW1lZCt4ZXJFRW1jZTNRU0FYOAoxeXd0WmJQWDBXa2hzNXVLTUNDOWtiQ0hmMkc2RUJteTJhYWtSWUFEcGlQV09RRldCREZvT1lPTTZUdXpoLzdMCnZGQUc5eU9nZzczVWVkOHB1K2t5U2J5NmQ2b1Y2L3p0N2gyNXdxcnBlT1VtcUlRMWh2MWl2QVhIYUdpdGxjQnMKYkVFdXp4eHk5RE9ZSFByVG14SXpmOUdaVDdwYWVBM1lMQkl5b2FFbFBKckxEMXJRbUVMYk5HbUY4TStQb1h1cApmcmx6Vk9DMFc3cjhFVTBUaEV0dEVUNXY5VHdlNTd4bTNvTGNTVWRtMXRWYllKSllQSTExWHljV2xNUlYrMFNHCjNGRCtHYmZYUHZUNm9JalRkbVg1R1A2R0U5eUgvNGh4M0VVR1M2dHYyMnIxM0NqZHVNWT0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=")
            .withClientKeyData("LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFcFFJQkFBS0NBUUVBMzJGUXUrNXdRVTNpOEYvTm5UUkNTTmkvY2xmaEdWMmg2NGpoS1k2V1pmRTY1MEY4CmFGM00wSXNkOGZjT0V2RG9UMTBJWnVVU21UOUhFdWVsZW9HU3BMREFEVk1UemN3Z1M0MXBOcEJWY1ZaUEFMc2cKMlViM0ZjVmJOZkU4ZXlUcU1LZVlEZmhqTS9mK2NyOTZXbjFRTkRSUnJpT0YxR1A2Yll5UUFVSlhOSVE1MVRjYgphS0V0SndvdkthMTRqZUpWbEUySUhPbC9vdjQvYVRaY1hHa0pIc3RKdWE4VHJ1QjlOd0xuT1UrY0p3ZEVXYXFYClZ2Q0FBTU5CcmFneTlhT3NDWTVPdGo5eGlOQTBEcXp4QmVOL0RKT3R6MTdHU3UvTkdWNlh6SFpBOG5teXJaaEsKYUJXWFhmbGc0S1ZsMTdtWHlrUjJ6Z0cwSDNheVRjWUFpOFRkNFFJREFRQUJBb0lCQUF1eHhZOUJLV0ZSMllxcQpDVktzbExmSm1TUThVNDJIYUYyeldjNWFKNmVJbklQVTJ0Vi82NlFUVkdzQmV4d2t2cFRCRXhxSDdaUldTcnRECm8xY3BoMnJWMGdnZ2pFM2UzT1gzcmtWc0F2MGUvcDNVTGlFUjVRZWZ3R212aW5JTCtiSzRZMjUvelhuQUJ2TXMKOFJQaHNadzZEUGluNVc2OEdUVTVBYVQvT3grRm41R1Y2bFJVRFpuNXJkaXJsNjQyZ3V3UU8wbm1MTVQ4dnN0TwpIbWRIQ21vcEF1MVkraitqRUY4TEttUlVHYVB0SkplQm5Ec0lhaWJveHJiWmkxSWY3c2pmVUdkVnlYdXdNUWRLCmpPUzNvdExidGZLYWxmcXhMQkt3RVQxbU9XcTEyWTcxSTVDQkV2MllPMGp4dzBvUDhTZVJUOFRVKzkzMnE2c00KS251SkVnMENnWUVBOVIrb0d2blFuVDB4Q0lGRitLZ2Z1ck94TkVITUpUOTV0dGx4ekhQazhZY3pjeFlrTzhDbwpmZkgrTlBrUzYrRmNLSW9ZVGdQZVNtOTVxSiszaE9UZ2hKV0RURThlMDFPRXJOR2VvYXh4K2NLWDd0dnJ1Z3EyCk9Jb1pWMnpwWWNMYVF3dzhieC9HMm9GcTJLNHhYVUkrTHNWSXZrMDE2bnFFcmZ3WEIrNXRZL01DZ1lFQTZVcXMKN1p5c2RFM2lhcHJkejVsOWs3OEY0VDFGbUR0RE1DUDFGVGF0bWJRamJ6S0NjVGRSSkRJR3dNT0ZTa1hvalQvLwoxdEdrZzEyVjRadGpmM1RnQ1ZMb09BQnh6N2RmQmd2RWdNeGlRbExTa3RTck1FVEQyM1FHcCtjRHE3VVdScHRwCmkrdUQwMnRzOXZ4SHNndUEvWGFDdnlJT0VUUHdvRzZsWVlzK2I5c0NnWUVBNkxUQitjSWFDM3lSTlRyTXU3Q1kKVnIzS3lBcUREUVdDdmxVV1ZQdmRhSEpySktIcmloSnZvd2wreExtRS9ndzNXb0VuYThEK1lub0w3RXFjaGZ4bgpMVmRuaTZVOVpYQlBiMldkakd6UzAwS3F6R2RhRlllZjBITkNkWjMrdmdHbkJhbnpJYWZ3TjNaUGdoOTAyODFNCmpVaGJzOVpIRGpCOEQxUllaUDQwT1lNQ2dZRUEyYUFmMFVqbVYxaVFib0lwaVEvV1pZMDlIdkRaaXprOVpCc20KOWJNY0h0WThkKzdXWUdjSitvbndZc0lDQlZkUnFQS2E1dnFLVFVGd0lCV2txN0dMalNjYUdhUFFoOFMza0J4SwpJeHlHVFBpV0Z3THM3d28yNnZGQmhEK3MzUEVBNXJ6enFPTDdCcTZmNTRkUUduZEF6VEZRcHB4T0l4NGd5b2h0CkhxR1dqU0VDZ1lFQTFRSDliVkM2Y3dUeEdtQTFOLzV1Q09qbm1ubEp1UXN3emw3bWZVeWpzL0NELzV2UysreVcKZFFYdkZaVXBPblJEeVhwOGl2SFdORHc3Y2swekRGSWMzWTNHWTA0c0lBMTN1b1NzTjBBd3RlWDNxemk5TUMydApFUEhncHpBWkdERUxiMEtqUzFMZlU0UnZiNjZudUtSd2ZsMWhKc3JEeFdZeWowNzhsVlBFNmVNPQotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo=")
            .withMasterUrl("https://133.133.135.32:6443")
            .build();

    public static ExtendedKubernetesClient getClient() throws Exception {
        return new ExtendedKubernetesClient(config);
    }

    public static VirtualMachine getVMByName(String name) throws Exception {
        return getClient().virtualMachines().get(name);
    }

    public static VirtualMachineBackup getVMBByName(String name) throws Exception {
        return getClient().virtualMachineBackups().get(name);
    }

    public static List<VirtualMachineBackup> getVMBList() throws Exception {
        return getClient().virtualMachineBackups().list().getItems();
    }

    public static List<VirtualMachineBackup> getVMBList(String domain) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("domain", domain);
        return getClient().virtualMachineBackups().list(map).getItems();
    }

    public static VirtualMachineImage getVMImageByName(String name) throws Exception {
        return getClient().virtualMachineImages().get(name);
    }

    public static VirtualMachineDisk getVMDiskByName(String name) throws Exception {
        return getClient().virtualMachineDisks().get(name);
    }

    public static VirtualMachinePool getVMPoolByName(String name) throws Exception {
        return getClient().virtualMachinePools().get(name);
    }

    public static VirtualMachineDiskImage getVMDiskImageByName(String name) throws Exception {
        return getClient().virtualMachineDiskImages().get(name);
    }

    public static VirtualMachineNetwork getVMNetworkByName(String name) throws Exception {
        return getClient().virtualMachineNetworks().get(name);
    }
}
