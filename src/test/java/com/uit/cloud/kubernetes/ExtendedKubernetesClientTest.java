/*
 * Copyright (2019, ) Institute of Software, Chinese Academy of Sciences
 */
package com.uit.cloud.kubernetes;

import java.util.List;

import com.uit.cloud.kubernetes.api.model.DoneableVirtualMachine;
import com.uit.cloud.kubernetes.api.model.VirtualMachine;
import com.uit.cloud.kubernetes.api.model.VirtualMachineList;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * @since  2019/5/1
 *
 * This code is used to manage CustomResource's lifecycle,
 * such as VirtualMachine
 */
public class ExtendedKubernetesClientTest {
	
	public static void main(String[] args) throws Exception {

		ExtendedKubernetesClient client = getClient();
//		getPods(client);
		watchVM(client);
//		KubernetesDeserializer.registerCustomKind("cloudplus.io/v1alpha3", "VirtualMachine", VirtualMachine.class);
		
//		listVM(client, crd);
//		CustomResourceDefinition crd = client.customResourceDefinitions().withName("virtualmachines.cloudplus.io").get();
//		System.out.println(crd);
//		client.customResource(crd, VirtualMachine.class, VirtualMachineList.class, DoneableVirtualMachine.class)
//						.inAnyNamespace().watch(new Watcher<VirtualMachine>() {
//
//				public void eventReceived(Action action, VirtualMachine resource) {
//					System.out.println(resource);
//				}
//
//				public void onClose(KubernetesClientException cause) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//			});
		
	}

	protected static void listVM(ExtendedKubernetesClient client, CustomResourceDefinition crd) {
		List<VirtualMachine> vms = client.customResource(crd, VirtualMachine.class, 
														VirtualMachineList.class, 
														DoneableVirtualMachine.class)
						.inAnyNamespace().list().getItems();
		for (VirtualMachine vm : vms) {
			System.out.println(vm);
		}
	}

	protected static void watchVM(ExtendedKubernetesClient client) {
		Watcher<VirtualMachine> watcher = new Watcher<VirtualMachine>() {

			public void eventReceived(Action action, VirtualMachine resource) {
				System.out.println(action + ":" + resource);
			}

			public void onClose(KubernetesClientException cause) {
				System.out.println(cause);
			}
			
		};
		client.watchVirtualMachine(watcher );
	}

	protected static void getPods(ExtendedKubernetesClient client) {
		for (Pod pod : client.pods().inAnyNamespace().list().getItems()) {
			System.out.println(pod.getMetadata().getName());
		}
	}

	protected static ExtendedKubernetesClient getClient() throws Exception {
		Config config = new ConfigBuilder()
						.withApiVersion("v1")
						.withCaCertData("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUN5RENDQWJDZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwcmRXSmwKY201bGRHVnpNQjRYRFRFNU1EWXhOREEyTURZMU4xb1hEVEk1TURZeE1UQTJNRFkxTjFvd0ZURVRNQkVHQTFVRQpBeE1LYTNWaVpYSnVaWFJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTURrCjZRaGowOGU3MTdGQWhmejE4UVdCcHQ0azNjUmtFU0V4YVR5QmR4YklPanBJN3VIYkwwODBkZXg1UENjSE5FdTQKKzJnamZnZWwxWENpWTdqYUV4c3hKM3Q2bVoxWmpQNXQ5dmE0cHJtTmN0bk0xTnlPMkNJelI2L1hyVkZNYmFRawpHV2pma0JjZjlHY2pTRlcxaENJVG9TS1RGL1JGbE1CU0dhNkVEeXh3MVErbHNVK1N2eFIzUVdRSHRnTEZnSElOCnBDRjRibzk4aDh2QWJkYUFOdzR6OEpMcVIrUnZtdlNCd3V5aEhXSi9nT1NvSTh6eElEZlRpV3lVWldDczBpZWoKZDM4TGhIbllZd05pZk5XMVorVGpYWkZiT2h3Ync5YzQ3VHpVUkM2UHFydmNabWVHTlZiMmhiQjBNSjNvajZadwp2WXJsMjhhWkxxNHFvN1pOVFRzQ0F3RUFBYU1qTUNFd0RnWURWUjBQQVFIL0JBUURBZ0trTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFBV2VyWWY5MHdHaWFwQWRXSGtHQlVYYzAvYUUKSFJkV3pTWCtnWE5aNzZZRzFySW9DeGhxbW9YVkhmenhhcUVwWEc4TWdGcWJaK3VFT2FJbTNaSS92Wkt5RWpYZQo1NVVON1YyZDRLb2dQR3BqejNTbzFBRHZhS2lyQjJYM1pVZStGcis2RUZMMjhOWjFjUTZBUXBoRHJkdUgvMHdrCmhIb2VXWUZoTDRBNGhTSDBLZC9QVUZtdHM2azRlNTViNGpFTFBKclZJT1VmanIrVVFqcElCNnphWFJPK3NDYjAKcnRsYmczUjBJQ1hPMHlvdzlNZGh1aE54R3JPR3JuN1VQWTExSFgvY0s3b3Z5WkVDMTE1THFxNUZmcy9wcDZocgoyaFU2Rm12L3JsTDcwVndobGZtNEd2VzI0RklNRHd1eVRBQUxrVHNNSGQ5MFhTU2k2a3FNVUV2VVFndz0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=")
						.withClientCertData("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUM4akNDQWRxZ0F3SUJBZ0lJSDlWNmpZVnZzOVl3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB4T1RBMk1UUXdOakEyTlRkYUZ3MHlNREEyTVRNd05qQTJOVGxhTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXRmRnZ2S1oxaXpvejRMbFgKcFZuY1Zlb0kzTDIvc0RlcTROYktkMnM5RitWOHJ0Z3hIaXd0TnZIcnUvVlZYQjREUXFDdHhiWStiTzVKckhyTQpGNTNjdXdRaDlibi9MWVV1ZCtZSTBYdGtqblpGZHZmM1RwS292c1czeTlrdC9TeWViOHBoWU1YOFVrU3UrQVBqCkpERkNrUWxsRXMwN25MMWVKcWRrN0pJOXlXL0ZtWUV5UndYSGNUdXdqQWl5QjJJUEFqNGdTdU5XSXB0Sm9iczcKSDQzU3d3dFhnZE8wNENSM3Q2bHlHdFZkSUdweExkbVFuLzYxUEZnMHRvWHlaZDlUbjMvNFphYURTbHF6K05BaQpqYlVDRXhVd0ErdFpxMGU4bWp0S0xDTkt1VDloZ2JuVDd1TDRrUE9JckRkanVYWXFYbExLTW9TODMxNGtFNUxrCnVPVVJQUUlEQVFBQm95Y3dKVEFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dFQkFNQ0MwRDdnS29zTmZUVi9UZ3dCL1lYMUtRc2xPbm9pYmlMbApmcEhlV0wva004OTZXS3NvU0JFcG1Ka0hySnVzMk9IOFFuUE9obkxBMVc3bldJbEQrWm0rNzNjYkZobGVIb0dIClpFSUpIWjU5T2prOGNyZkhGTE9kZGtqN1BPU3BmaXgvSnpWY3hnM3VhQnZvbkRHVHFuQ1ovalJ4VXJSSE5tMDAKVzVEbWMxWjcrQzM3Qzc0Z1F1UjIxVXltTHhiYzFPWEFmcnFLRWtIUTNHQ0E0NWwwYzcxbUFBYkMxMmczWFNoNQpvc3dxWHNjWHNnbEVDQU0yOTNXR0NiTk54S2Z2VEllMlowMzhIc2VGREJ0T0VSMk5rMTJmakZPOWYrUXVXdkluCnBmU1VmdC9FQ2EyL0RQOGhjZ3hFakV6a0J6M0RHb0NhYUJqaUFla2VXZ3pKMlpkM0U5az0KLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=")
						.withClientKeyData("LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFcFFJQkFBS0NBUUVBdGZGdnZLWjFpem96NExsWHBWbmNWZW9JM0wyL3NEZXE0TmJLZDJzOUYrVjhydGd4Ckhpd3ROdkhydS9WVlhCNERRcUN0eGJZK2JPNUpySHJNRjUzY3V3UWg5Ym4vTFlVdWQrWUkwWHRram5aRmR2ZjMKVHBLb3ZzVzN5OWt0L1N5ZWI4cGhZTVg4VWtTdStBUGpKREZDa1FsbEVzMDduTDFlSnFkazdKSTl5Vy9GbVlFeQpSd1hIY1R1d2pBaXlCMklQQWo0Z1N1TldJcHRKb2JzN0g0M1N3d3RYZ2RPMDRDUjN0Nmx5R3RWZElHcHhMZG1RCm4vNjFQRmcwdG9YeVpkOVRuMy80WmFhRFNscXorTkFpamJVQ0V4VXdBK3RacTBlOG1qdEtMQ05LdVQ5aGdiblQKN3VMNGtQT0lyRGRqdVhZcVhsTEtNb1M4MzE0a0U1TGt1T1VSUFFJREFRQUJBb0lCQVFDc0lvU0lneVJxV09LRQpGbFpweGg3RDJld1FqQ1V6WHpkNENUNzh4S1RFV3dZZmxjTEN2U0Q1L3QvOHNCdldUejdlWm82Qm8rNWp1UDVUCkJNcmEvU3U4ZENoOUJ6LzVuU3RkbkhGelg3Ni9XZjVXbER1U1J3Y0hscUJSYUdRdVIyeU9iM2E1ekJidzdySnIKUzZJMnQ3UTI3Q0NJTFV2Yyt2eDhyWnE2aDVHVlpXVnowM0p1d20yOGg0NnRUVWNkaGE3eTkydHE1aG02ZEhSYwpKTVZCUC8xaUEralZoWlZtdG5IRXdBbTVzTXg2WHRzZ0ZlNUZhSDhjY2FqRUxIT1FBYUg2cmtyOVVqeFhVUStICldjQlZ2K0pKT3VBUVJUbVRUNWpDd0w5RVYxOVJUSDc2U3NGM2hkRVAzeUlZSTZpVlVOQktOMDZaUlFwRmVFM24KclpqaGlkMkJBb0dCQU4vaXA4N3Y2KzVHeE5VdzEwNEZRS3g3Z0RXbVM0TFZ2Rmc2NWJsVTdURzA4a2RES1pscQpNTFAxNjg4MU5Vb0poS2d2TlMwTnI1MEwrQTVyWWk0eUxhay9SbGNKQ3ozS2FyYmlsNUVhQXU1TWlKaktpVUNnCjNJNW5OMnVxd1kxNHpyRVFJdEhXbDIwV3IrbWl4N0RxUnhVVnhOblluaFpOYk9UeU82T0tDRnY1QW9HQkFOQUsKbkIvVmhTMTZ4WTN1R0RoUWZiZjQrN2o5Tk9YSFFVZFBncFEvUERUbitjNGhDd0Z1aVo3UTRLdktuTnlYMlpkQgpkUzNlQk9QaHdyMVE4cTlLdm4xU1RVS0ViNURWeHVkbW5HdG5QZ3JSMzZ3enFlWFBDc3hhdGdxMk05eWdxR0I3CmwwSEVnQ2pQUkREdkZtcEJMbitjaE1sQjlLUU1wN2xmODVWNndRaGxBb0dCQUp0ejVIS3ljazRyUTBhSU9DY0wKVEtHMnl3bjFZcDhBeTFzejRnaHlhSlBJQVNYc0EzbHJtMCtKWk9lbUdVOUVGcUUwemladlIwMnpYdFNjU1RxcwpTcm9tbmkrV3J3Q0RpTjlkckVIckhyZloya3JFN3RJNkg0cWRMb1VLdG9RRnF2YVVycWltNk5PdEdnNE9vYnM4Cm9JaUthZm1kQi8rU1dSY0svdEFKNnMzNUFvR0JBSlpaaEpsUkUyMVowOW9OM3owYmxxL1ZaZjQ4Si9XRHVmNlMKVExsY3RlTTZYd09FUjlMaUV0MU84WC9WN3VWUmJMUnVYd1FsOXZ6RjFKcERIUkJvQVNESzBRRW9ld1IrS3NCagpITnBXQTdXSUZaRDZ5V2RHNlBQay9yamhFcnY0ODVhKzJ3Snh3M2s2eVhwZjM2QXN1VEVLYWNDVlJQc21GalRYClRrK1NKbDB0QW9HQUxaSjZRSjljQUp1emFPNXZFRFZzNGMwT1JQbG9lQVNURklkOU04RkF1Z1NvdWdnSEpKa0IKS1NKc0RlQXAybzd1YnFRNlpCak1SUlpSUWxOeWtQQmlaMHgyaHdRbkViVkxvTkdWWXFWTEo2VlVlUUZabllmdQpUZDdFbXVMZ2N6VmtISXgvSzBLb0tlcklNZFE1RytDTC92Z3lrNXB4Z3lVU2NjRDVnNnhoUWRBPQotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo=")
						.withMasterUrl("https://133.133.135.22:6443")
						.build();
				
		return new ExtendedKubernetesClient(config);
	}
	
}
