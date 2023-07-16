import router from "@/router";
import useStore from "@/stores";
import { getToken } from "@/utils/token";
import NProgress from "nprogress";

NProgress.configure({
  easing: "ease",
  speed: 500,
  showSpinner: false,
  trickleSpeed: 200,
  minimum: 0.3,
});

router.beforeEach((to, from, next) => {
  NProgress.start();
  const { user } = useStore();
  if (to.meta.title) {
    document.title = to.meta.title;
  }
  if (getToken()) {    
    if (user.id === undefined) {
      user.doUserInfo()
        .then(() => next())
        .catch(() => {
          user.doLogout().then(() => {
            window.$message?.warning("凭证失效，请重新登录");
            next();
          });
        });
    } else {
      next();
    }
  } else {
    next();
  }
});
router.afterEach(() => {
  NProgress.done();
});
